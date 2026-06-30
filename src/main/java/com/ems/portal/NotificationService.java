package com.ems.portal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

record NotificationItem(
        Long id,
        String kind,
        String senderCode,
        String senderName,
        String recipientCode,
        String title,
        String subject,
        String message,
        String priority,
        String category,
        String status,
        String channel,
        boolean read,
        String createdAt
) {}

record NotificationPageResponse(
        List<NotificationItem> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        long unreadCount
) {}

@Service
class NotificationService {

    private final InAppNotificationRepository repository;

    NotificationService(InAppNotificationRepository repository) {
        this.repository = repository;
    }

    NotificationPageResponse list(String employeeCode, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = clampSize(size);
        long total = count(employeeCode);
        long unread = unreadCount(employeeCode);
        List<NotificationItem> items = fetch(employeeCode, safePage * safeSize, safeSize);
        int totalPages = safeSize == 0 ? 0 : (int) Math.ceil(total / (double) safeSize);
        return new NotificationPageResponse(items, safePage, safeSize, total, totalPages, unread);
    }

    List<NotificationItem> latest(String employeeCode, int limit) {
        return fetch(employeeCode, 0, clampSize(limit));
    }

    long unreadCount(String employeeCode) {
        return repository.countByEmployeeCodeAndReadFalse(employeeCode);
    }

    @Transactional
    public void markAllRead(String employeeCode) {
        List<InAppNotification> all = repository.findByEmployeeCodeAndReadFalseOrderByCreatedAtDesc(employeeCode);
        for (InAppNotification n : all) {
            n.setRead(true);
            repository.save(n);
        }
    }

    @Transactional
    Map<String, Object> markRead(String employeeCode, String messageKey) {
        MessageRef ref = parse(messageKey);
        var opt = repository.findByIdAndEmployeeCode(ref.id(), employeeCode);
        if (opt.isPresent()) {
            InAppNotification notif = opt.get();
            notif.setRead(true);
            repository.save(notif);
            return Map.of("updated", 1, "message", "Marked as read");
        }
        return Map.of("updated", 0, "message", "Notification not found");
    }

    private long count(String employeeCode) {
        return repository.findByEmployeeCodeAndReadFalseOrderByCreatedAtDesc(employeeCode).size();
    }

    private List<NotificationItem> fetch(String employeeCode, int offset, int limit) {
        List<InAppNotification> all = repository.findByEmployeeCodeAndReadFalseOrderByCreatedAtDesc(employeeCode);
        List<NotificationItem> items = new ArrayList<>();
        int end = Math.min(offset + limit, all.size());
        for (int i = offset; i < end; i++) {
            InAppNotification n = all.get(i);
            items.add(new NotificationItem(
                    n.getId(), "IN_APP", null, "System", n.getEmployeeCode(),
                    n.getTitle(), null, n.getMessage(),
                    "MEDIUM", "General", n.getRead() ? "READ" : "UNREAD", "IN_APP",
                    n.getRead(), n.getCreatedAt() != null ? n.getCreatedAt().toString() : LocalDateTime.now().toString()
            ));
        }
        return items;
    }

    private int clampSize(int size) {
        if (size <= 0) return 10;
        return Math.min(size, 100);
    }

    private MessageRef parse(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            return new MessageRef("DIRECT", 0L);
        }
        String[] parts = messageKey.split(":", 2);
        if (parts.length == 2) {
            try {
                return new MessageRef(parts[0], Long.parseLong(parts[1]));
            } catch (NumberFormatException ignored) {
                return new MessageRef(parts[0], 0L);
            }
        }
        try {
            return new MessageRef("DIRECT", Long.parseLong(messageKey));
        } catch (NumberFormatException ignored) {
            return new MessageRef("DIRECT", 0L);
        }
    }

    private record MessageRef(String kind, Long id) {}
}
