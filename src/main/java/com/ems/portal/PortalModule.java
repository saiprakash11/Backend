package com.ems.portal;

import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import com.ems.employee.entity.EmployeeProfile;
import com.ems.employee.entity.NotificationPreference;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.employee.repository.NotificationPreferenceRepository;
import com.ems.hr.performance.ManagerFeedback;
import com.ems.hr.performance.ManagerFeedbackRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

record PortalProfileResponse(
        String employeeCode,
        String fullName,
        String email,
        String role,
        String department,
        String designation,
        String phoneNumber,
        String dateOfBirth,
        String dateOfJoining,
        String reportingManagerCode,
        String workLocation,
        String photoUrl,
        String residentialAddress,
        String emergencyContactName,
        String emergencyContactPhone,
        long unreadCount,
        long sentCount,
        long receivedCount,
        boolean emailAlerts,
        boolean pushAlerts,
        boolean payrollAlerts,
        String digestFrequency,
        String summary
) {}

record PortalNotificationItem(
        Long id,
        String kind,
        String direction,
        String title,
        String message,
        String source,
        String audience,
        String priority,
        String category,
        String status,
        boolean read,
        String createdAt,
        String sendDate,
        String expiryDate,
        String channel,
        String senderCode,
        String recipientCode
) {}

record PortalFeedItem(
        Long id,
        String kind,
        String title,
        String message,
        String audience,
        String source,
        String createdAt
) {}

record PortalPreferencesResponse(
        boolean emailAlerts,
        boolean pushAlerts,
        boolean payrollAlerts,
        String digestFrequency
) {}

record PortalCenterResponse(
        PortalProfileResponse profile,
        PortalPreferencesResponse preferences,
        Map<String, Object> stats,
        List<PortalNotificationItem> inbox,
        List<PortalNotificationItem> sent,
        List<PortalFeedItem> feed
) {}

record PortalMessageRequest(
        String title,
        String subject,
        String message,
        String priority,
        String category,
        String sendDate,
        String expiryDate,
        String recipientType,
        String recipientRole,
        String recipientCode,
        String channel
) {}

record PortalProfileUpdateRequest(
        String fullName,
        String email,
        String phoneNumber,
        String department,
        String designation,
        String workLocation,
        String reportingManagerCode,
        String photoUrl,
        String residentialAddress,
        String emergencyContactName,
        String emergencyContactPhone,
        String dateOfBirth,
        String dateOfJoining,
        Boolean emailAlerts,
        Boolean pushAlerts,
        Boolean payrollAlerts,
        String digestFrequency
) {}

@Service
class PortalService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository profileRepository;
    private final InAppNotificationRepository inAppNotificationRepository;
    private final NotificationMessageRepository notificationMessageRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final AnnouncementRepository announcementRepository;
    private final CompanyEventRepository companyEventRepository;
    private final ManagerFeedbackRepository managerFeedbackRepository;
    private final ActivityLogRepository activityLogRepository;
    private final EntityManager em;

    PortalService(UserRepository userRepository, EmployeeProfileRepository profileRepository,
                  InAppNotificationRepository inAppNotificationRepository,
                  NotificationMessageRepository notificationMessageRepository,
                  NotificationPreferenceRepository notificationPreferenceRepository,
                  AnnouncementRepository announcementRepository,
                  CompanyEventRepository companyEventRepository,
                  ManagerFeedbackRepository managerFeedbackRepository,
                  ActivityLogRepository activityLogRepository,
                  EntityManager em) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.inAppNotificationRepository = inAppNotificationRepository;
        this.notificationMessageRepository = notificationMessageRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.announcementRepository = announcementRepository;
        this.companyEventRepository = companyEventRepository;
        this.managerFeedbackRepository = managerFeedbackRepository;
        this.activityLogRepository = activityLogRepository;
        this.em = em;
    }

    PortalProfileResponse getProfile(String employeeCode) {
        User user = userRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + employeeCode));

        EmployeeProfile profile = profileRepository.findByEmployeeCode(employeeCode);
        if (profile == null) {
            profile = buildDefaultProfile(user);
        }

        PortalPreferencesResponse preferences = loadPreferences(employeeCode);
        long unreadCount = countUnread(employeeCode);
        long sentCount = notificationMessageRepository.countBySenderCode(employeeCode);
        long receivedCount = notificationMessageRepository.countByRecipientCodeAndDeletedAtIsNull(employeeCode);

        return new PortalProfileResponse(
                employeeCode,
                valueOr(profile.getFullName(), displayName(user)),
                valueOr(user.getEmail(), user.getUsername()),
                safeRole(user.getRole()),
                valueOr(profile.getDepartment(), defaultDepartment(user.getRole())),
                valueOr(profile.getDesignation(), defaultDesignation(user.getRole())),
                profile.getPhoneNumber(),
                formatDate(profile.getDateOfBirth()),
                formatDate(profile.getDateOfJoining()),
                profile.getReportingManagerCode(),
                profile.getWorkLocation(),
                user.getProfilePhoto(),
                profile.getResidentialAddress(),
                profile.getEmergencyContactName(),
                profile.getEmergencyContactPhone(),
                unreadCount,
                sentCount,
                receivedCount,
                preferences.emailAlerts(),
                preferences.pushAlerts(),
                preferences.payrollAlerts(),
                preferences.digestFrequency(),
                "Manage your role-specific profile, message center, and feed."
        );
    }

    PortalProfileResponse updateProfile(String employeeCode, PortalProfileUpdateRequest request) {
        User user = userRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + employeeCode));

        EmployeeProfile profile = profileRepository.findByEmployeeCode(employeeCode);
        boolean newProfile = false;
        if (profile == null) {
            profile = buildDefaultProfile(user);
            newProfile = true;
        }

        if (request.fullName() != null && !request.fullName().isBlank()) {
            profile.setFullName(request.fullName().trim());
        }
        if (request.phoneNumber() != null) profile.setPhoneNumber(blankToNull(request.phoneNumber()));
        if (request.department() != null && !request.department().isBlank()) profile.setDepartment(request.department().trim());
        if (request.designation() != null && !request.designation().isBlank()) profile.setDesignation(request.designation().trim());
        if (request.workLocation() != null) profile.setWorkLocation(blankToNull(request.workLocation()));
        if (request.reportingManagerCode() != null) profile.setReportingManagerCode(blankToNull(request.reportingManagerCode()));
        if (request.photoUrl() != null) user.setProfilePhoto(blankToNull(request.photoUrl()));
        if (request.residentialAddress() != null) profile.setResidentialAddress(blankToNull(request.residentialAddress()));
        if (request.emergencyContactName() != null) profile.setEmergencyContactName(blankToNull(request.emergencyContactName()));
        if (request.emergencyContactPhone() != null) profile.setEmergencyContactPhone(blankToNull(request.emergencyContactPhone()));
        if (request.dateOfBirth() != null && !request.dateOfBirth().isBlank()) {
            profile.setDateOfBirth(parseDate(request.dateOfBirth()));
        }
        if (request.dateOfJoining() != null && !request.dateOfJoining().isBlank()) {
            profile.setDateOfJoining(parseDate(request.dateOfJoining()));
        } else if (newProfile && profile.getDateOfJoining() == null) {
            profile.setDateOfJoining(LocalDate.now());
        }
        if (profile.getDateOfJoining() == null) {
            profile.setDateOfJoining(LocalDate.now());
        }

        profileRepository.save(profile);

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email().trim());
        }
        userRepository.save(user);

        savePreferences(employeeCode, request);
        return getProfile(employeeCode);
    }

    PortalCenterResponse getCenter(String employeeCode) {
        PortalProfileResponse profile = getProfile(employeeCode);
        PortalPreferencesResponse preferences = loadPreferences(employeeCode);
        List<PortalNotificationItem> inbox = loadInbox(employeeCode);
        List<PortalNotificationItem> sent = loadSent(employeeCode);
        List<PortalFeedItem> feed = loadFeed(employeeCode, profile.role());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("unread", inbox.stream().filter(item -> !item.read()).count());
        stats.put("sent", sent.size());
        stats.put("received", inbox.size());
        stats.put("feed", feed.size());

        return new PortalCenterResponse(profile, preferences, stats, inbox, sent, feed);
    }

    Map<String, Object> sendMessage(String senderCode, PortalMessageRequest request) {
        User sender = userRepository.findByEmployeeCode(senderCode)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderCode));

        List<String> recipientCodes = resolveRecipientCodes(request.recipientCode(), request.recipientRole(), request.recipientType());
        if (recipientCodes.isEmpty()) {
            throw new IllegalArgumentException("No recipients matched the selection.");
        }

        String title = valueOr(request.title(), valueOr(request.subject(), "Notification"));
        String subject = valueOr(request.subject(), title);
        String message = valueOr(request.message(), "");
        String channel = normalizeChannel(request.channel());
        String senderName = displayName(sender);
        String recipientRole = normalizeRecipientRole(request.recipientRole());
        String recipientType = normalizeRecipientType(request.recipientType(), request.recipientCode(), request.recipientRole());
        String priority = normalizePriority(request.priority());
        String category = valueOr(blankToNull(request.category()), "General");
        LocalDate sendDate = parseDateOrNull(request.sendDate());
        LocalDate expiryDate = parseDateOrNull(request.expiryDate());

        int inserted = 0;
        for (String recipientCode : recipientCodes) {
            NotificationMessage nm = new NotificationMessage();
            nm.setSenderCode(senderCode);
            nm.setSenderName(senderName);
            nm.setRecipientCode(recipientCode);
            nm.setRecipientRole(recipientRole);
            nm.setRecipientType(recipientType);
            nm.setTitle(title);
            nm.setSubject(subject);
            nm.setMessage(message);
            nm.setPriority(priority);
            nm.setCategory(category);
            nm.setSendDate(sendDate != null ? sendDate.atStartOfDay() : LocalDateTime.now());
            nm.setExpiryDate(expiryDate != null ? expiryDate.atStartOfDay() : null);
            nm.setStatus("UNREAD");
            nm.setChannel(channel);
            nm.setRead(false);
            notificationMessageRepository.save(nm);

            InAppNotification ian = new InAppNotification();
            ian.setEmployeeCode(recipientCode);
            ian.setTitle(title);
            ian.setMessage(message);
            ian.setTimeCategory("TODAY");
            ian.setRead(false);
            inAppNotificationRepository.save(ian);

            inserted++;
        }

        return Map.of(
                "message", "Message sent successfully",
                "recipients", recipientCodes.size(),
                "stored", inserted,
                "channel", channel,
                "priority", priority,
                "category", category
        );
    }

    Map<String, Object> markMessageRead(String employeeCode, String messageKey) {
        int updated = 0;
        String kind = null;
        Long messageId = null;
        if (messageKey != null) {
            String[] parts = messageKey.split(":", 2);
            if (parts.length == 2) {
                kind = parts[0];
                try { messageId = Long.parseLong(parts[1]); } catch (NumberFormatException ignored) {}
            } else {
                try { messageId = Long.parseLong(messageKey); } catch (NumberFormatException ignored) {}
            }
        }

        if ("IN_APP".equalsIgnoreCase(kind) && messageId != null) {
            Optional<InAppNotification> n = inAppNotificationRepository.findByIdAndEmployeeCode(messageId, employeeCode);
            if (n.isPresent()) {
                n.get().setRead(true);
                inAppNotificationRepository.save(n.get());
                updated = 1;
            }
        } else if ("DIRECT".equalsIgnoreCase(kind) && messageId != null) {
            Optional<NotificationMessage> m = notificationMessageRepository.findById(messageId);
            if (m.isPresent() && employeeCode.equals(m.get().getRecipientCode())) {
                m.get().setRead(true);
                m.get().setStatus("READ");
                notificationMessageRepository.save(m.get());
                updated = 1;
            }
        } else if (messageId != null) {
            Optional<NotificationMessage> m = notificationMessageRepository.findById(messageId);
            if (m.isPresent() && employeeCode.equals(m.get().getRecipientCode())) {
                m.get().setRead(true);
                m.get().setStatus("READ");
                notificationMessageRepository.save(m.get());
                updated = 1;
            }
            if (updated == 0) {
                Optional<InAppNotification> n = inAppNotificationRepository.findByIdAndEmployeeCode(messageId, employeeCode);
                if (n.isPresent()) {
                    n.get().setRead(true);
                    inAppNotificationRepository.save(n.get());
                    updated = 1;
                }
            }
        }

        return Map.of("updated", updated, "message", updated > 0 ? "Marked as read" : "Message not found");
    }

    Map<String, Object> markMessageUnread(String employeeCode, String messageKey) {
        Long messageId = extractMessageId(messageKey);
        if (messageId == null) {
            return Map.of("updated", 0, "message", "Message not found");
        }
        int updated = 0;
        Optional<NotificationMessage> m = notificationMessageRepository.findById(messageId);
        if (m.isPresent() && employeeCode.equals(m.get().getRecipientCode())) {
            m.get().setRead(false);
            m.get().setStatus("UNREAD");
            notificationMessageRepository.save(m.get());
            updated = 1;
        }
        if (updated == 0) {
            Optional<InAppNotification> n = inAppNotificationRepository.findByIdAndEmployeeCode(messageId, employeeCode);
            if (n.isPresent()) {
                n.get().setRead(false);
                inAppNotificationRepository.save(n.get());
                updated = 1;
            }
        }
        return Map.of("updated", updated, "message", updated > 0 ? "Marked as unread" : "Message not found");
    }

    Map<String, Object> archiveMessage(String employeeCode, String messageKey) {
        Long messageId = extractMessageId(messageKey);
        if (messageId == null) {
            return Map.of("updated", 0, "message", "Message not found");
        }
        int updated = 0;
        Optional<NotificationMessage> m = notificationMessageRepository.findById(messageId);
        if (m.isPresent() && employeeCode.equals(m.get().getRecipientCode())) {
            m.get().setStatus("ARCHIVED");
            m.get().setArchivedAt(LocalDateTime.now());
            notificationMessageRepository.save(m.get());
            updated = 1;
        }
        return Map.of("updated", updated, "message", updated > 0 ? "Archived" : "Message not found");
    }

    Map<String, Object> deleteMessage(String employeeCode, String messageKey) {
        Long messageId = extractMessageId(messageKey);
        if (messageId == null) {
            return Map.of("updated", 0, "message", "Message not found");
        }
        int updated = 0;
        Optional<NotificationMessage> m = notificationMessageRepository.findById(messageId);
        if (m.isPresent() && employeeCode.equals(m.get().getRecipientCode())) {
            m.get().setDeletedAt(LocalDateTime.now());
            notificationMessageRepository.save(m.get());
            updated = 1;
        }
        if (updated == 0) {
            Optional<InAppNotification> n = inAppNotificationRepository.findByIdAndEmployeeCode(messageId, employeeCode);
            if (n.isPresent()) {
                inAppNotificationRepository.delete(n.get());
                updated = 1;
            }
        }
        return Map.of("updated", updated, "message", updated > 0 ? "Deleted" : "Message not found");
    }

    private PortalPreferencesResponse loadPreferences(String employeeCode) {
        try {
            Object[] row = (Object[]) em.createNativeQuery(
                    "SELECT email_alerts, push_alerts, payroll_alerts, digest_frequency FROM notification_preferences WHERE employee_code = ?"
            ).setParameter(1, employeeCode).getSingleResult();
            return new PortalPreferencesResponse(
                    boolValue(row[0], true),
                    boolValue(row[1], true),
                    boolValue(row[2], true),
                    valueOr((String) row[3], "realtime")
            );
        } catch (NoResultException | jakarta.persistence.NonUniqueResultException ex) {
            return new PortalPreferencesResponse(true, true, true, "realtime");
        }
    }

    @SuppressWarnings("unchecked")
    private void savePreferences(String employeeCode, PortalProfileUpdateRequest request) {
        boolean emailAlerts = request.emailAlerts() == null || request.emailAlerts();
        boolean pushAlerts = request.pushAlerts() == null || request.pushAlerts();
        boolean payrollAlerts = request.payrollAlerts() == null || request.payrollAlerts();
        String digestFrequency = normalizeDigestFrequency(request.digestFrequency());

        em.createNativeQuery(
                "INSERT INTO notification_preferences (employee_code, email_alerts, push_alerts, payroll_alerts, digest_frequency) " +
                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE email_alerts = VALUES(email_alerts), " +
                "push_alerts = VALUES(push_alerts), payroll_alerts = VALUES(payroll_alerts), " +
                "digest_frequency = VALUES(digest_frequency), updated_at = CURRENT_TIMESTAMP"
        ).setParameter(1, employeeCode)
         .setParameter(2, emailAlerts)
         .setParameter(3, pushAlerts)
         .setParameter(4, payrollAlerts)
         .setParameter(5, digestFrequency)
         .executeUpdate();
    }

    private long countUnread(String employeeCode) {
        try {
            Long inApp = (Long) em.createNativeQuery(
                    "SELECT COUNT(*) FROM in_app_notifications WHERE employee_code = ? AND is_read = false"
            ).setParameter(1, employeeCode).getSingleResult();
            Long direct = (Long) em.createNativeQuery(
                    "SELECT COUNT(*) FROM notification_messages WHERE recipient_code = ? AND is_read = false AND deleted_at IS NULL"
            ).setParameter(1, employeeCode).getSingleResult();
            return (inApp != null ? inApp : 0L) + (direct != null ? direct : 0L);
        } catch (Exception ex) {
            long c1 = 0;
            long c2 = 0;
            try {
                c1 = ((Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM in_app_notifications WHERE employee_code = ? AND is_read = 0"
                ).setParameter(1, employeeCode).getSingleResult()).longValue();
            } catch (Exception ignored) {}
            try {
                c2 = ((Number) em.createNativeQuery(
                        "SELECT COUNT(*) FROM notification_messages WHERE recipient_code = ? AND is_read = 0 AND deleted_at IS NULL"
                ).setParameter(1, employeeCode).getSingleResult()).longValue();
            } catch (Exception ignored) {}
            return c1 + c2;
        }
    }

    private List<PortalNotificationItem> loadInbox(String employeeCode) {
        List<PortalNotificationItem> items = new ArrayList<>();

        List<InAppNotification> inApps = inAppNotificationRepository.findByEmployeeCodeOrderByCreatedAtDesc(employeeCode);
        for (InAppNotification n : inApps) {
            items.add(new PortalNotificationItem(
                    n.getId(), "IN_APP", "RECEIVED",
                    n.getTitle(), n.getMessage(), "System",
                    n.getTimeCategory(), "MEDIUM", n.getTimeCategory(),
                    n.getRead() ? "READ" : "UNREAD", n.getRead(),
                    stringify(n.getCreatedAt()),
                    n.getCreatedAt() != null ? n.getCreatedAt().toLocalDate().toString() : null,
                    null, "IN_APP", null, employeeCode
            ));
        }

        List<NotificationMessage> msgs = notificationMessageRepository.findByRecipientCodeAndDeletedAtIsNullOrderByCreatedAtDesc(employeeCode);
        for (NotificationMessage m : msgs) {
            items.add(new PortalNotificationItem(
                    m.getId(), "DIRECT", "RECEIVED",
                    valueOr(m.getTitle(), m.getSubject()), m.getMessage(),
                    valueOr(m.getSenderName(), m.getSenderCode()),
                    valueOr(m.getRecipientRole(), m.getRecipientType()),
                    m.getPriority(), m.getCategory(),
                    valueOr(m.getStatus(), m.getRead() ? "READ" : "UNREAD"),
                    m.getRead() != null && m.getRead(),
                    stringify(m.getCreatedAt()),
                    m.getSendDate() != null ? m.getSendDate().toLocalDate().toString() : null,
                    m.getExpiryDate() != null ? m.getExpiryDate().toLocalDate().toString() : null,
                    m.getChannel(), m.getSenderCode(), m.getRecipientCode()
            ));
        }

        return items.stream()
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                .collect(Collectors.toList());
    }

    private List<PortalNotificationItem> loadSent(String employeeCode) {
        return notificationMessageRepository.findBySenderCodeOrderByCreatedAtDesc(employeeCode).stream()
                .map(m -> new PortalNotificationItem(
                        m.getId(), "DIRECT", "SENT",
                        valueOr(m.getTitle(), m.getSubject()), m.getMessage(),
                        valueOr(m.getSenderName(), m.getSenderCode()),
                        valueOr(m.getRecipientRole(), m.getRecipientType()),
                        m.getPriority(), m.getCategory(),
                        valueOr(m.getStatus(), m.getRead() ? "READ" : "UNREAD"),
                        m.getRead() != null && m.getRead(),
                        stringify(m.getCreatedAt()),
                        m.getSendDate() != null ? m.getSendDate().toLocalDate().toString() : null,
                        m.getExpiryDate() != null ? m.getExpiryDate().toLocalDate().toString() : null,
                        m.getChannel(), m.getSenderCode(), m.getRecipientCode()
                ))
                .collect(Collectors.toList());
    }

    private List<PortalFeedItem> loadFeed(String employeeCode, String role) {
        List<PortalFeedItem> feed = new ArrayList<>();
        String normalizedRole = safeRole(role);

        List<Announcement> announcements = announcementRepository.findByAudienceInOrderByPublishedAtDesc(List.of("ALL", feedAudience(normalizedRole)));
        for (Announcement a : announcements) {
            feed.add(new PortalFeedItem(
                    a.getId(), "ANNOUNCEMENT",
                    a.getTitle(), a.getBody(),
                    a.getAudience(),
                    valueOr(a.getPublishedBy(), "System"),
                    stringify(a.getPublishedAt())
            ));
        }

        List<CompanyEvent> events = companyEventRepository.findAllByOrderByEventDateDescCreatedAtDesc();
        for (CompanyEvent e : events) {
            feed.add(new PortalFeedItem(
                    e.getId(), "EVENT",
                    e.getEventName(), e.getDescription(),
                    "ALL", "System",
                    stringify(e.getCreatedAt())
            ));
        }

        List<ManagerFeedback> feedbacks = managerFeedbackRepository.findByEmployeeCodeOrderByCreatedAtDesc(employeeCode);
        for (ManagerFeedback f : feedbacks) {
            feed.add(new PortalFeedItem(
                    f.getId(), "FEEDBACK",
                    valueOr(f.getQuarter(), "Manager feedback"),
                    f.getFeedback(),
                    "PERSONAL",
                    valueOr(f.getManagerCode(), "Manager"),
                    stringify(f.getCreatedAt())
            ));
        }

        List<ActivityLog> logs = activityLogRepository.findByEmployeeCodeOrderByCreatedAtDesc(employeeCode);
        for (ActivityLog l : logs) {
            feed.add(new PortalFeedItem(
                    l.getId(), "ACTIVITY",
                    valueOr(l.getActivityType(), "Activity"),
                    l.getDescription(),
                    "PERSONAL", "System",
                    stringify(l.getCreatedAt())
            ));
        }

        return feed.stream()
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))
                .collect(Collectors.toList());
    }

    private Long extractMessageId(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) return null;
        String[] parts = messageKey.split(":", 2);
        String raw = parts.length == 2 ? parts[1] : messageKey;
        try { return Long.parseLong(raw); } catch (NumberFormatException ex) { return null; }
    }

    private EmployeeProfile buildDefaultProfile(User user) {
        EmployeeProfile profile = new EmployeeProfile();
        profile.setEmployeeCode(user.getEmployeeCode());
        profile.setFullName(displayName(user));
        profile.setDepartment(defaultDepartment(user.getRole()));
        profile.setDesignation(defaultDesignation(user.getRole()));
        profile.setDateOfJoining(LocalDate.now());
        profile.setEmail(null);
        return profile;
    }

    private List<String> resolveRecipientCodes(String recipientCode, String recipientRole, String recipientType) {
        if (recipientCode != null && !recipientCode.isBlank()) {
            return List.of(recipientCode.trim());
        }

        String normalizedType = normalizeRecipientType(recipientType, recipientCode, recipientRole);
        String normalizedRole = normalizeRecipientRole(recipientRole);

        if ("ALL".equals(normalizedType)) {
            return userRepository.findEmployeeCodesByRoleIn(List.of("ADMIN", "HR", "MANAGER", "EMPLOYEE"));
        }

        if ("TEAM".equals(normalizedType) || "DEPARTMENT".equals(normalizedType) || "MANAGER".equals(normalizedRole)) {
            return userRepository.findEmployeeCodesByRoleIn(List.of("MANAGER", "MANAGEMENT"));
        }

        if ("HR".equals(normalizedRole)) {
            return userRepository.findEmployeeCodesByRoleIn(List.of("ADMIN", "HR"));
        }

        if ("EMPLOYEE".equals(normalizedRole)) {
            return userRepository.findEmployeeCodesByRole("EMPLOYEE");
        }

        return List.of();
    }

    private String normalizeRecipientRole(String recipientRole) {
        return switch (safeRole(recipientRole)) {
            case "ADMIN", "HR", "MANAGER", "EMPLOYEE" -> safeRole(recipientRole);
            case "MANAGEMENT" -> "MANAGER";
            case "HR_ADMIN" -> "HR";
            default -> "ALL";
        };
    }

    private String feedAudience(String role) {
        return switch (safeRole(role)) {
            case "MANAGER", "MANAGEMENT" -> "MANAGEMENT";
            case "HR", "ADMIN", "HR_ADMIN" -> "HR";
            default -> "EMPLOYEE";
        };
    }

    private String normalizeChannel(String channel) {
        if (channel == null || channel.isBlank()) return "IN_APP";
        return switch (channel.trim().toUpperCase(Locale.ROOT)) {
            case "EMAIL", "SMS" -> channel.trim().toUpperCase(Locale.ROOT);
            default -> "IN_APP";
        };
    }

    private String normalizeRecipientType(String recipientType, String recipientCode, String recipientRole) {
        if (recipientCode != null && !recipientCode.isBlank()) return "INDIVIDUAL";
        return switch (safeRole(recipientType)) {
            case "INDIVIDUAL", "DEPARTMENT", "TEAM", "ALL" -> safeRole(recipientType);
            case "MANAGEMENT", "MANAGER" -> "TEAM";
            case "HR", "ADMIN", "HR_ADMIN" -> "DEPARTMENT";
            default -> "ALL";
        };
    }

    private String normalizePriority(String priority) {
        if (priority == null || priority.isBlank()) return "MEDIUM";
        return switch (priority.trim().toUpperCase(Locale.ROOT)) {
            case "LOW", "MEDIUM", "HIGH", "CRITICAL" -> priority.trim().toUpperCase(Locale.ROOT);
            default -> "MEDIUM";
        };
    }

    private String normalizeDigestFrequency(String digestFrequency) {
        if (digestFrequency == null || digestFrequency.isBlank()) return "realtime";
        return switch (digestFrequency.trim().toLowerCase(Locale.ROOT)) {
            case "daily", "weekly", "realtime" -> digestFrequency.trim().toLowerCase(Locale.ROOT);
            default -> "realtime";
        };
    }

    private String safeRole(String role) {
        return role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
    }

    private String defaultDepartment(String role) {
        return switch (safeRole(role)) {
            case "MANAGER", "MANAGEMENT" -> "Management";
            case "HR", "ADMIN", "HR_ADMIN" -> "HR Administration";
            default -> "Corporate";
        };
    }

    private String defaultDesignation(String role) {
        return switch (safeRole(role)) {
            case "MANAGER", "MANAGEMENT" -> "Manager";
            case "HR", "ADMIN", "HR_ADMIN" -> "HR Administrator";
            default -> "Employee";
        };
    }

    private String displayName(User user) {
        if (user.getUsername() != null && !user.getUsername().isBlank()) return user.getUsername();
        if (user.getEmployeeCode() != null && !user.getEmployeeCode().isBlank()) return user.getEmployeeCode();
        return valueOr(user.getEmail(), "Team Member");
    }

    private String valueOr(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Boolean boolValue(Object value, boolean fallback) {
        if (value == null) return fallback;
        if (value instanceof Boolean b) return b;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    private LocalDate parseDate(String value) {
        try { return LocalDate.parse(value.trim()); } catch (DateTimeParseException ex) { return LocalDate.now(); }
    }

    private LocalDate parseDateOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        try { return LocalDate.parse(value.trim()); } catch (DateTimeParseException ex) { return null; }
    }

    private String formatDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    private String stringify(LocalDateTime timestamp) {
        return timestamp == null ? LocalDateTime.now().toString() : timestamp.toString();
    }
}

@RestController
@RequestMapping("/api/portal")
class PortalController {

    private final PortalService service;

    PortalController(PortalService service) {
        this.service = service;
    }

    @GetMapping("/profile/{employeeCode}")
    public ResponseEntity<?> getProfile(@PathVariable String employeeCode) {
        try {
            return ResponseEntity.ok(service.getProfile(employeeCode));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/profile/{employeeCode}")
    public ResponseEntity<?> updateProfile(
            @PathVariable String employeeCode,
            @RequestBody PortalProfileUpdateRequest request) {
        try {
            return ResponseEntity.ok(service.updateProfile(employeeCode, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/notifications/{employeeCode}")
    public ResponseEntity<?> getNotifications(@PathVariable String employeeCode) {
        try {
            return ResponseEntity.ok(service.getCenter(employeeCode));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/notifications/{employeeCode}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable String employeeCode,
            @RequestBody PortalMessageRequest request) {
        try {
            return ResponseEntity.ok(service.sendMessage(employeeCode, request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/notifications/{employeeCode}/messages/{messageId}/read")
    public ResponseEntity<?> markRead(
            @PathVariable String employeeCode,
            @PathVariable String messageId) {
        return ResponseEntity.ok(service.markMessageRead(employeeCode, messageId));
    }

    @PutMapping("/notifications/{employeeCode}/messages/{messageId}/unread")
    public ResponseEntity<?> markUnread(
            @PathVariable String employeeCode,
            @PathVariable String messageId) {
        return ResponseEntity.ok(service.markMessageUnread(employeeCode, messageId));
    }

    @PutMapping("/notifications/{employeeCode}/messages/{messageId}/archive")
    public ResponseEntity<?> archive(
            @PathVariable String employeeCode,
            @PathVariable String messageId) {
        return ResponseEntity.ok(service.archiveMessage(employeeCode, messageId));
    }

    @PutMapping("/notifications/{employeeCode}/messages/{messageId}/delete")
    public ResponseEntity<?> delete(
            @PathVariable String employeeCode,
            @PathVariable String messageId) {
        return ResponseEntity.ok(service.deleteMessage(employeeCode, messageId));
    }
}