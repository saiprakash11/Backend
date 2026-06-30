package com.ems.employee.controller;

import com.ems.employee.entity.EmployeeProfile;
import com.ems.employee.entity.NotificationPreference;
import com.ems.employee.entity.PriorityTask;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.employee.repository.NotificationPreferenceRepository;
import com.ems.employee.repository.PriorityTaskRepository;
import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import com.ems.hr.attendance.AttendanceBreakLog;
import com.ems.hr.attendance.AttendanceBreakLogRepository;
import com.ems.hr.attendance.AttendanceRecord;
import com.ems.hr.attendance.AttendanceRecordRepository;
import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeRepository;
import com.ems.hr.documents.EmployeeDocument;
import com.ems.hr.documents.EmployeeDocumentRepository;
import com.ems.employee.entity.EmployeeProfilePhoto;
import com.ems.employee.repository.EmployeeProfilePhotoRepository;
import com.ems.hr.leave.EmployeeLeaveRequest;
import com.ems.hr.leave.EmployeeLeaveRequestRepository;
import com.ems.hr.leave.LeaveBalance;
import com.ems.hr.leave.LeaveBalanceRepository;
import com.ems.hr.leave.LeaveTypeRepository;
import com.ems.hr.payroll.PayslipRepository;
import com.ems.hr.performance.ManagerFeedback;
import com.ems.hr.performance.ManagerFeedbackRepository;
import com.ems.hr.performance.PerformanceGoal;
import com.ems.hr.performance.PerformanceGoalRepository;
import com.ems.portal.InAppNotification;
import com.ems.portal.InAppNotificationRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/employees/{employeeCode}")
public class EmployeeDashboardApiController {

    private final EmployeeProfileRepository      employeeProfileRepository;
    private final UserRepository                 userRepository;
    private final EmployeeRepository             employeeRepository;
    private final AttendanceRecordRepository     attendanceRecordRepository;
    private final AttendanceBreakLogRepository   attendanceBreakLogRepository;
    private final LeaveBalanceRepository         leaveBalanceRepository;
    private final EmployeeLeaveRequestRepository employeeLeaveRequestRepository;
    private final PriorityTaskRepository         priorityTaskRepository;
    private final ManagerFeedbackRepository      managerFeedbackRepository;
    private final InAppNotificationRepository    inAppNotificationRepository;
    private final EmployeeDocumentRepository     employeeDocumentRepository;
    private final PerformanceGoalRepository      performanceGoalRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final LeaveTypeRepository            leaveTypeRepository;
    private final EmployeeProfilePhotoRepository  employeeProfilePhotoRepository;
    private final PayslipRepository               payslipRepository;

    public EmployeeDashboardApiController(
            EmployeeProfileRepository employeeProfileRepository,
            UserRepository userRepository,
            EmployeeRepository employeeRepository,
            AttendanceRecordRepository attendanceRecordRepository,
            AttendanceBreakLogRepository attendanceBreakLogRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            EmployeeLeaveRequestRepository employeeLeaveRequestRepository,
            PriorityTaskRepository priorityTaskRepository,
            ManagerFeedbackRepository managerFeedbackRepository,
            InAppNotificationRepository inAppNotificationRepository,
            EmployeeDocumentRepository employeeDocumentRepository,
            PerformanceGoalRepository performanceGoalRepository,
            NotificationPreferenceRepository notificationPreferenceRepository,
            LeaveTypeRepository leaveTypeRepository,
            EmployeeProfilePhotoRepository employeeProfilePhotoRepository,
            PayslipRepository payslipRepository) {
        this.employeeProfileRepository = employeeProfileRepository;
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.attendanceBreakLogRepository = attendanceBreakLogRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeLeaveRequestRepository = employeeLeaveRequestRepository;
        this.priorityTaskRepository = priorityTaskRepository;
        this.managerFeedbackRepository = managerFeedbackRepository;
        this.inAppNotificationRepository = inAppNotificationRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.performanceGoalRepository = performanceGoalRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeProfilePhotoRepository = employeeProfilePhotoRepository;
        this.payslipRepository = payslipRepository;
    }

    // ── Dashboard ─────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);
        LocalDate today = today();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("profile", profile(employeeCode));
        data.put("todayAttendance", attendanceRecordRepository
                .findByEmployeeCodeAndWorkDate(employeeCode, today).orElse(null));
        data.put("todayBreaks", attendanceBreakLogRepository
                .findByEmployeeCodeAndWorkDateOrderByBreakStartAsc(employeeCode, today));
        data.put("leaveBalance", leaveBalance(employeeCode));
        data.put("pendingLeaveDays", employeeLeaveRequestRepository
                .sumPendingDaysByEmployeeCode(employeeCode));
        data.put("tasks", priorityTaskRepository
                .findTop3ByEmployeeCodeOrderByDueDateAsc(employeeCode));
        data.put("feedback", managerFeedbackRepository
                .findTop2ByEmployeeCodeOrderByReviewDateDesc(employeeCode));
        data.put("unreadNotifications", inAppNotificationRepository
                .countByEmployeeCodeAndReadFalse(employeeCode));

        payslipRepository.findByEmployeeCodeOrderByPayrollMonthDesc(employeeCode)
                .stream().findFirst().ifPresent(slip -> {
                    Map<String, Object> pay = new LinkedHashMap<>();
                    pay.put("id", slip.getId());
                    pay.put("employee_code", slip.getEmployeeCode());
                    pay.put("payroll_month", slip.getPayrollMonth());
                    pay.put("basic_salary", slip.getBasicSalary());
                    pay.put("hra", slip.getHra());
                    pay.put("allowances", slip.getAllowances());
                    pay.put("pf_deduction", slip.getPfDeduction());
                    pay.put("tax_deduction", slip.getTaxDeduction());
                    pay.put("other_deductions", slip.getOtherDeductions());
                    pay.put("net_salary", slip.getNetSalary());
                    pay.put("currency_code", slip.getCurrencyCode() != null ? slip.getCurrencyCode() : "INR");
                    pay.put("status", slip.getStatus());
                    pay.put("generated_at", slip.getGeneratedAt() != null
                            ? slip.getGeneratedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                            : null);
                    data.put("payroll", pay);
                });

        return data;
    }

    // ── Attendance ────────────────────────────────────────────────────

    @GetMapping("/attendance")
    public Map<String, Object> attendance(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        LocalDate monthStart = today().withDayOfMonth(1);
        List<AttendanceRecord> records = attendanceRecordRepository
                .findByEmployeeCodeAndWorkDateGreaterThanEqualOrderByWorkDateDesc(employeeCode, monthStart);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("records", records);
        data.put("presentDays", records.stream()
                .filter(r -> !"Absent".equals(r.getStatus()))
                .count());
        data.put("lateDays", records.stream()
                .filter(r -> "Late".equals(r.getStatus()))
                .count());

        String avgCheckIn = records.stream()
                .filter(r -> r.getCheckIn() != null)
                .map(r -> r.getCheckIn().toSecondOfDay())
                .mapToInt(Integer::intValue)
                .average()
                .stream()
                .mapToObj(avgSecs -> {
                    int h = (int) avgSecs / 3600;
                    int m = ((int) avgSecs % 3600) / 60;
                    return String.format("%02d:%02d", h, m);
                })
                .findFirst()
                .orElse(null);
        data.put("averageCheckIn", avgCheckIn);
        data.put("currentStreak", currentStreak(employeeCode));

        return data;
    }

    @PostMapping("/attendance/check-in")
    public ResponseEntity<Map<String, Object>> checkIn(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        LocalDate today = today();
        LocalTime current = now();
        String status = current.isAfter(LocalTime.of(9, 15)) ? "Late" : "Present";

        AttendanceRecord record = attendanceRecordRepository
                .findByEmployeeCodeAndWorkDate(employeeCode, today)
                .orElseGet(() -> {
                    AttendanceRecord r = new AttendanceRecord();
                    r.setEmployeeCode(employeeCode);
                    r.setWorkDate(today);
                    return r;
                });

        if (record.getCheckIn() == null) {
            record.setCheckIn(current);
            record.setStatus(status);
            attendanceRecordRepository.save(record);

            if ("Late".equals(status)) {
                InAppNotification notif = new InAppNotification();
                notif.setEmployeeCode(employeeCode);
                notif.setTitle("Late Check-In");
                notif.setMessage("You checked in late at " + current + ". Please arrive on time tomorrow.");
                notif.setNotificationType("ATTENDANCE");
                notif.setTimeCategory("TODAY");
                inAppNotificationRepository.save(notif);
            }
        }

        return ResponseEntity.ok(attendance(employeeCode));
    }

    @PostMapping("/attendance/check-out")
    public ResponseEntity<Map<String, Object>> checkOut(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        LocalDate today = today();
        LocalTime current = now();

        AttendanceRecord record = attendanceRecordRepository
                .findByEmployeeCodeAndWorkDate(employeeCode, today)
                .orElse(null);

        if (record != null && record.getCheckIn() != null) {
            record.setCheckOut(current);
            long minutes = ChronoUnit.MINUTES.between(record.getCheckIn(), current);
            record.setTotalHours(BigDecimal.valueOf(minutes / 60.0));
            attendanceRecordRepository.save(record);
        }

        return ResponseEntity.ok(attendance(employeeCode));
    }

    // ── Attendance Breaks ─────────────────────────────────────────────

    @PostMapping("/attendance/break/start")
    public ResponseEntity<?> breakStart(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        LocalDate today = today();
        LocalTime current = now();

        Optional<AttendanceBreakLog> active = attendanceBreakLogRepository
                .findByEmployeeCodeAndWorkDateAndBreakEndIsNull(employeeCode, today);
        if (active.isPresent()) {
            return ResponseEntity.badRequest().body("A break is already running.");
        }

        AttendanceBreakLog breakLog = new AttendanceBreakLog();
        breakLog.setEmployeeCode(employeeCode);
        breakLog.setWorkDate(today);
        breakLog.setBreakStart(current);
        attendanceBreakLogRepository.save(breakLog);

        return ResponseEntity.ok(Map.of("break_start", current.toString()));
    }

    @PostMapping("/attendance/break/stop")
    public ResponseEntity<?> breakStop(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        LocalDate today = today();
        LocalTime current = now();

        Optional<AttendanceBreakLog> active = attendanceBreakLogRepository
                .findByEmployeeCodeAndWorkDateAndBreakEndIsNull(employeeCode, today);
        if (active.isEmpty()) {
            return ResponseEntity.badRequest().body("No active break found.");
        }

        AttendanceBreakLog breakLog = active.get();
        breakLog.setBreakEnd(current);
        attendanceBreakLogRepository.save(breakLog);

        return ResponseEntity.ok(Map.of("break_end", current.toString()));
    }

    // ── Leave ─────────────────────────────────────────────────────────

    @GetMapping("/leave")
    public ResponseEntity<?> leave(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("balance", leaveBalance(employeeCode));
            data.put("requests", employeeLeaveRequestRepository
                    .findByEmployeeCodeOrderByCreatedAtDesc(employeeCode));
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load leave data: " + e.getMessage()));
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<?> applyLeave(
            @PathVariable String employeeCode,
            @RequestBody Map<String, String> request) {
        requireEmployeeAccess(employeeCode);

        String leaveType = normalizeLeaveType(request.getOrDefault("leaveType", ""));
        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(request.get("startDate"));
            endDate = LocalDate.parse(request.get("endDate"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Please provide a valid date range.");
        }

        String reason = request.getOrDefault("reason", "").trim();
        if (leaveType.isBlank() || reason.isBlank() || endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                    .body("Please provide a valid leave type, date range, and reason.");
        }

        int numberOfDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
        Map<String, Object> profile = profile(employeeCode);
        String employeeName = (String) profile.getOrDefault("fullName",
                profile.getOrDefault("full_name", employeeCode));

        EmployeeLeaveRequest leaveRequest = new EmployeeLeaveRequest();
        leaveRequest.setEmployeeCode(employeeCode);
        leaveRequest.setEmployeeId(employeeCode);
        leaveRequest.setEmployeeName(employeeName);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setFromDate(startDate);
        leaveRequest.setToDate(endDate);
        leaveRequest.setNumberOfDays(numberOfDays);
        leaveRequest.setReason(reason);
        leaveRequest.setStatus("Pending");
        employeeLeaveRequestRepository.save(leaveRequest);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("balance", leaveBalance(employeeCode));
        data.put("requests", employeeLeaveRequestRepository
                .findByEmployeeCodeOrderByCreatedAtDesc(employeeCode));
        return ResponseEntity.ok(data);
    }

    // ── Performance ───────────────────────────────────────────────────

    @GetMapping("/performance")
    public Map<String, Object> performance(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("goals", performanceGoalRepository
                .findByEmployeeCodeOrderByQuarterDescUpdatedAtDesc(employeeCode));
        data.put("feedback", managerFeedbackRepository
                .findByEmployeeCodeOrderByReviewDateDesc(employeeCode));
        data.put("rating", managerFeedbackRepository
                .averageRatingByEmployeeCode(employeeCode));
        return data;
    }

    // ── Profile ───────────────────────────────────────────────────────

    @GetMapping("/profile")
    public Map<String, Object> profilePage(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("profile", profile(employeeCode));
        data.put("documents", employeeDocumentRepository
                .findByEmployeeCodeOrderByUploadedAtDesc(employeeCode));
        data.put("preferences", preferences(employeeCode));
        return data;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable String employeeCode,
            @RequestBody Map<String, String> request) {
        requireEmployeeAccess(employeeCode);

        EmployeeProfile ep = employeeProfileRepository.findByEmployeeCode(employeeCode);
        if (ep == null) {
            ep = new EmployeeProfile();
            ep.setEmployeeCode(employeeCode);
        }

        if (request.containsKey("fullName")) ep.setFullName(request.get("fullName"));
        if (request.containsKey("phoneNumber")) ep.setPhoneNumber(request.get("phoneNumber"));
        if (request.containsKey("dateOfBirth")) {
            try { ep.setDateOfBirth(LocalDate.parse(request.get("dateOfBirth"))); } catch (Exception ignored) {}
        }
        if (request.containsKey("residentialAddress")) ep.setResidentialAddress(request.get("residentialAddress"));
        if (request.containsKey("emergencyContactName")) ep.setEmergencyContactName(request.get("emergencyContactName"));
        if (request.containsKey("emergencyContactPhone")) ep.setEmergencyContactPhone(request.get("emergencyContactPhone"));
        if (request.containsKey("email")) {
            ep.setEmail(request.get("email"));
            userRepository.findByEmployeeCode(employeeCode).ifPresent(u -> {
                u.setUsername(request.get("email"));
                userRepository.save(u);
            });
        }

        employeeProfileRepository.save(ep);

        return ResponseEntity.ok(profilePage(employeeCode));
    }

    @PostMapping("/profile-photo")
    @Transactional
    public ResponseEntity<?> uploadProfilePhoto(
            @PathVariable String employeeCode,
            @RequestParam("photo") MultipartFile photo) {
        requireEmployeeAccess(employeeCode);

        if (photo.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a profile photo.");
        }

        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body("Only image files are allowed.");
        }

        try {
            Optional<User> userOpt = userRepository.findByEmployeeCode(employeeCode);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found.");
            }

            User user = userOpt.get();
            String photoUrl = "/api/employees/" + employeeCode + "/profile-photo";
            user.setProfilePhoto(photoUrl);
            user.setProfilePhotoName(photo.getOriginalFilename());
            user.setProfilePhotoContentType(contentType);
            userRepository.save(user);

            employeeProfilePhotoRepository.deleteByEmployeeCode(employeeCode);

            EmployeeProfilePhoto profilePhoto = new EmployeeProfilePhoto(
                    employeeCode, photo.getOriginalFilename(), contentType, photo.getBytes());
            employeeProfilePhotoRepository.save(profilePhoto);

            return ResponseEntity.ok(Map.of("photo", photoUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload profile photo: " + e.getMessage());
        }
    }

    @GetMapping(value = "/profile-photo", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/webp"})
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable String employeeCode) {
        Optional<EmployeeProfilePhoto> photoOpt = employeeProfilePhotoRepository
                .findTopByEmployeeCodeAndIsActiveTrueOrderByUploadedAtDesc(employeeCode);
        if (photoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EmployeeProfilePhoto photo = photoOpt.get();
        MediaType mt = photo.getContentType() != null
                ? MediaType.parseMediaType(photo.getContentType())
                : MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(mt)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(photo.getPhotoData());
    }

    // ── Documents ─────────────────────────────────────────────────────

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @PathVariable String employeeCode,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", defaultValue = "General") String documentType,
            @RequestParam(value = "documentName", required = false) String documentName) {
        requireEmployeeAccess(employeeCode);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            String originalName = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
            String displayName = (documentName == null || documentName.isBlank()) ? originalName : documentName.trim();

            EmployeeDocument doc = new EmployeeDocument();
            doc.setEmployeeCode(employeeCode);
            doc.setDocumentName(displayName);
            doc.setDocumentType(documentType);
            doc.setDocumentData(file.getBytes());
            doc.setContentType(file.getContentType());
            doc = employeeDocumentRepository.save(doc);
            doc.setFilePath("/api/employees/" + employeeCode + "/documents/" + doc.getId() + "/download");
            doc = employeeDocumentRepository.save(doc);

            return ResponseEntity.ok(Map.of(
                    "id", doc.getId(),
                    "documentName", doc.getDocumentName(),
                    "documentType", doc.getDocumentType(),
                    "filePath", "/api/employees/" + employeeCode + "/documents/" + doc.getId() + "/download"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload document: " + e.getMessage());
        }
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable String employeeCode,
            @PathVariable Long documentId) {
        Optional<EmployeeDocument> docOpt = employeeDocumentRepository.findById(documentId);
        if (docOpt.isEmpty() || !docOpt.get().getEmployeeCode().equals(employeeCode)) {
            return ResponseEntity.notFound().build();
        }

        EmployeeDocument doc = docOpt.get();
        MediaType mt = doc.getContentType() != null
                ? MediaType.parseMediaType(doc.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mt)
                .header("Content-Disposition", "inline; filename=\"" +
                        (doc.getDocumentName() != null ? doc.getDocumentName() : "document") + "\"")
                .body(doc.getDocumentData());
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<?> deleteDocument(
            @PathVariable String employeeCode,
            @PathVariable Long documentId) {
        requireEmployeeAccess(employeeCode);

        Optional<EmployeeDocument> docOpt = employeeDocumentRepository.findById(documentId);
        if (docOpt.isEmpty() || !docOpt.get().getEmployeeCode().equals(employeeCode)) {
            return ResponseEntity.notFound().build();
        }

        EmployeeDocument doc = docOpt.get();
        employeeDocumentRepository.deleteById(documentId);

        return ResponseEntity.ok(Map.of("deleted", true, "id", documentId));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<EmployeeDocument>> getDocuments(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);
        return ResponseEntity.ok(employeeDocumentRepository
                .findByEmployeeCodeOrderByUploadedAtDesc(employeeCode));
    }

    // ── Notifications ─────────────────────────────────────────────────

    @GetMapping("/notifications")
    public Map<String, Object> notifications(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("today", inAppNotificationRepository
                .findByEmployeeCodeAndTimeCategoryOrderByCreatedAtDesc(employeeCode, "TODAY"));
        data.put("thisWeek", inAppNotificationRepository
                .findByEmployeeCodeAndTimeCategoryOrderByCreatedAtDesc(employeeCode, "THIS WEEK"));
        return data;
    }

    // ── Payslips ───────────────────────────────────────────────────────

    @GetMapping("/payslips")
    public ResponseEntity<?> payslips(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        return ResponseEntity.ok(payslipRepository.findByEmployeeCodeOrderByPayrollMonthDesc(employeeCode));
    }

    // ── Training ───────────────────────────────────────────────────────

    @GetMapping("/training")
    public ResponseEntity<?> training(@PathVariable String employeeCode) {
        requireEmployeeAccess(employeeCode);

        return ResponseEntity.ok(Map.of(
                "learningCourses", List.of(),
                "availableCourses", List.of(),
                "certificates", List.of(),
                "stats", Map.of("inProgress", 0, "completed", 0, "certificates", 0, "hours", 0)
        ));
    }

    // ── Preferences ───────────────────────────────────────────────────

    @PutMapping("/preferences")
    public ResponseEntity<Map<String, Object>> updatePreferences(
            @PathVariable String employeeCode,
            @RequestBody Map<String, Object> request) {
        requireEmployeeAccess(employeeCode);

        NotificationPreference pref = notificationPreferenceRepository.findById(employeeCode)
                .orElseGet(() -> {
                    NotificationPreference p = new NotificationPreference();
                    p.setEmployeeCode(employeeCode);
                    return p;
                });

        if (request.containsKey("notifyLeaveStatus")) pref.setNotifyLeaveStatus(bool(request.get("notifyLeaveStatus")));
        if (request.containsKey("notifyPayslip")) pref.setNotifyPayslip(bool(request.get("notifyPayslip")));
        if (request.containsKey("notifyPerformanceReminders")) pref.setNotifyPerformanceReminders(bool(request.get("notifyPerformanceReminders")));
        if (request.containsKey("notifyAnnouncements")) pref.setNotifyAnnouncements(bool(request.get("notifyAnnouncements")));
        if (request.containsKey("notifyAttendanceReminders")) pref.setNotifyAttendanceReminders(bool(request.get("notifyAttendanceReminders")));
        if (request.containsKey("digestFrequency")) pref.setDigestFrequency(String.valueOf(request.get("digestFrequency")));

        notificationPreferenceRepository.save(pref);

        return ResponseEntity.ok(profilePage(employeeCode));
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Map<String, Object> profile(String employeeCode) {
        User user = userRepository.findByEmployeeCode(employeeCode).orElse(null);
        EmployeeProfile ep = employeeProfileRepository.findByEmployeeCode(employeeCode);
        Employee emp = employeeRepository.findById(employeeCode).orElse(null);

        Map<String, Object> result = new LinkedHashMap<>();
        if (user != null) {
            result.put("employee_code", user.getEmployeeCode());
            result.put("employeeCode", user.getEmployeeCode());
            result.put("email", user.getUsername());
            result.put("role", user.getRole());
            result.put("photo", user.getProfilePhoto() != null
                    ? "/api/employees/" + employeeCode + "/profile-photo" : null);
        }
        if (ep != null) {
            result.put("fullName", ep.getFullName());
            result.put("full_name", ep.getFullName());
            result.put("department", ep.getDepartment());
            result.put("designation", ep.getDesignation());
            result.put("date_of_joining", ep.getDateOfJoining() != null ? ep.getDateOfJoining().toString() : null);
            result.put("dateOfJoining", ep.getDateOfJoining());
            result.put("phone_number", ep.getPhoneNumber());
            result.put("phoneNumber", ep.getPhoneNumber());
            result.put("date_of_birth", ep.getDateOfBirth() != null ? ep.getDateOfBirth().toString() : null);
            result.put("dateOfBirth", ep.getDateOfBirth());
            result.put("residential_address", ep.getResidentialAddress());
            result.put("residentialAddress", ep.getResidentialAddress());
            result.put("emergency_contact_name", ep.getEmergencyContactName());
            result.put("emergencyContactName", ep.getEmergencyContactName());
            result.put("emergency_contact_phone", ep.getEmergencyContactPhone());
            result.put("emergencyContactPhone", ep.getEmergencyContactPhone());
            result.put("reporting_manager_code", ep.getReportingManagerCode());
            result.put("reportingManagerCode", ep.getReportingManagerCode());
            result.put("work_location", ep.getWorkLocation());
            result.put("workLocation", ep.getWorkLocation());
        } else if (emp != null) {
            result.putIfAbsent("fullName", emp.getName());
            result.putIfAbsent("full_name", emp.getName());
            result.putIfAbsent("department", emp.getDepartment());
            result.putIfAbsent("designation", emp.getDesignation());
            result.putIfAbsent("email", emp.getEmail());
        }
        return result;
    }

    private Map<String, Object> leaveBalance(String employeeCode) {
        LeaveBalance balance = leaveBalanceRepository.findById(employeeCode)
                .orElseGet(() -> {
                    LeaveBalance lb = new LeaveBalance();
                    lb.setEmployeeCode(employeeCode);
                    lb.setAnnualLeave(18);
                    lb.setSickLeave(12);
                    lb.setCasualLeave(10);
                    lb.setPrivilegeLeave(15);
                    lb.setLossOfPay(5);
                    lb.setUsedAnnual(0);
                    lb.setUsedSick(0);
                    lb.setUsedCasual(0);
                    lb.setYear(LocalDate.now().getYear());
                    return leaveBalanceRepository.save(lb);
                });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employee_code", balance.getEmployeeCode());
        result.put("annual_leave", balance.getAnnualLeave());
        result.put("annualLeave", balance.getAnnualLeave());
        result.put("sick_leave", balance.getSickLeave());
        result.put("sickLeave", balance.getSickLeave());
        result.put("casual_leave", balance.getCasualLeave());
        result.put("casualLeave", balance.getCasualLeave());
        result.put("privilege_leave", balance.getPrivilegeLeave());
        result.put("privilegeLeave", balance.getPrivilegeLeave());
        result.put("loss_of_pay", balance.getLossOfPay());
        result.put("lossOfPay", balance.getLossOfPay());
        result.put("used_annual", balance.getUsedAnnual());
        result.put("usedAnnual", balance.getUsedAnnual());
        result.put("used_sick", balance.getUsedSick());
        result.put("usedSick", balance.getUsedSick());
        result.put("used_casual", balance.getUsedCasual());
        result.put("usedCasual", balance.getUsedCasual());
        result.put("year", balance.getYear());
        return result;
    }

    private Map<String, Object> preferences(String employeeCode) {
        NotificationPreference pref = notificationPreferenceRepository.findById(employeeCode)
                .orElseGet(() -> {
                    NotificationPreference p = new NotificationPreference();
                    p.setEmployeeCode(employeeCode);
                    return notificationPreferenceRepository.save(p);
                });

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("employee_code", pref.getEmployeeCode());
        result.put("notify_leave_status", pref.getNotifyLeaveStatus());
        result.put("notify_payslip", pref.getNotifyPayslip());
        result.put("notify_performance_reminders", pref.getNotifyPerformanceReminders());
        result.put("notify_announcements", pref.getNotifyAnnouncements());
        result.put("notify_attendance_reminders", pref.getNotifyAttendanceReminders());
        result.put("digest_frequency", pref.getDigestFrequency());
        return result;
    }

    private int currentStreak(String employeeCode) {
        List<AttendanceRecord> rows = attendanceRecordRepository
                .findByEmployeeCodeAndStatusNotOrderByWorkDateDesc(employeeCode, "Absent");

        int streak = 0;
        LocalDate expected = today();

        for (AttendanceRecord row : rows) {
            LocalDate workDate = row.getWorkDate();
            if (workDate.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (workDate.isBefore(expected)) {
                break;
            }
        }
        return streak;
    }

    private String currentEmployeeCode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    private String normalizeLeaveType(String leaveType) {
        String candidate = leaveType == null ? "" : leaveType.trim();
        if (candidate.isBlank()) return "";
        try {
            return leaveTypeRepository.findByTypeCodeIgnoreCase(candidate)
                    .map(lt -> lt.getTypeName())
                    .orElseGet(() -> leaveTypeRepository.findByTypeNameIgnoreCase(candidate)
                            .map(lt -> lt.getTypeName())
                            .orElse(candidate));
        } catch (Exception ex) {
            return candidate;
        }
    }

    private static LocalDate today() {
        LocalDate date = LocalDate.now();
        if (LocalTime.now().isBefore(LocalTime.of(5, 0))) {
            date = date.minusDays(1);
        }
        return date;
    }

    private static LocalTime now() {
        return LocalTime.now().withNano(0);
    }

    private void requireEmployeeAccess(String employeeCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        String currentEmployeeCode = authentication.getName();
        if (currentEmployeeCode != null && currentEmployeeCode.equalsIgnoreCase(employeeCode)) {
            return;
        }

        boolean privileged = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .anyMatch(a -> "ROLE_ADMIN".equals(a) || "ROLE_HR".equals(a));

        if (privileged) return;

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own employee records.");
    }

    private boolean bool(Object value) {
        return Boolean.TRUE.equals(value) || "true".equals(String.valueOf(value));
    }
}
