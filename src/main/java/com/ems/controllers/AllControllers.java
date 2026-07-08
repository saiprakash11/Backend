package com.ems.controllers;

// =====================================================================================
// AllControllers.java
// Auto-consolidated: every @RestController in the EMS backend, combined into one file.
// Generated from the individual controllers under com.ems.* — see EMS_API_Reference.md
// for the human-readable list of every endpoint grouped by module.
// =====================================================================================

import com.ems.auth.dto.ChangePasswordRequest;
import com.ems.auth.dto.ForgotPasswordRequest;
import com.ems.auth.dto.LoginRequest;
import com.ems.auth.dto.LoginResponse;
import com.ems.auth.dto.ResetPasswordRequest;
import com.ems.auth.dto.VerifyOtpRequest;
import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import com.ems.auth.service.AuthService;
import com.ems.employee.entity.EmployeeProfile;
import com.ems.employee.entity.EmployeeProfilePhoto;
import com.ems.employee.entity.NotificationPreference;
import com.ems.employee.entity.PriorityTask;
import com.ems.employee.repository.EmployeeProfilePhotoRepository;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.employee.repository.NotificationPreferenceRepository;
import com.ems.employee.repository.PriorityTaskRepository;
import com.ems.employee.service.EmployeeProfileService;
import com.ems.hr.assets.Asset;
import com.ems.hr.assets.AssetService;
import com.ems.hr.attendance.AttendanceBreakLog;
import com.ems.hr.attendance.AttendanceBreakLogRepository;
import com.ems.hr.attendance.AttendanceCorrectionService;
import com.ems.hr.attendance.AttendanceRecord;
import com.ems.hr.attendance.AttendanceRecordRepository;
import com.ems.hr.attendance.AttendanceService;
import com.ems.hr.attendance.ManagementAttendanceService;
import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeRepository;
import com.ems.hr.common.EmployeeStore;
import com.ems.hr.dashboard.DashboardMetricsService;
import com.ems.hr.dashboard.DashboardService;
import com.ems.hr.dashboard.DashboardSummary;
import com.ems.hr.documents.AdminDocumentService;
import com.ems.hr.documents.EmployeeDocument;
import com.ems.hr.documents.EmployeeDocumentRepository;
import com.ems.hr.leave.EmployeeLeaveRequest;
import com.ems.hr.leave.EmployeeLeaveRequestRepository;
import com.ems.hr.leave.LeaveBalance;
import com.ems.hr.leave.LeaveBalanceRepository;
import com.ems.hr.leave.LeaveRequest;
import com.ems.hr.leave.LeaveService;
import com.ems.hr.leave.LeaveType;
import com.ems.hr.leave.LeaveTypeRepository;
import com.ems.hr.onboarding.OnboardingService;
import com.ems.hr.onboarding.OnboardingSession;
import com.ems.hr.onboarding.OnboardingTracking;
import com.ems.hr.onboarding.OnboardingTrackingRepository;
import com.ems.hr.payroll.Payslip;
import com.ems.hr.payroll.PayslipDTO;
import com.ems.hr.payroll.PayslipRepository;
import com.ems.hr.performance.ManagerFeedback;
import com.ems.hr.performance.ManagerFeedbackRepository;
import com.ems.hr.performance.PerformanceGoal;
import com.ems.hr.performance.PerformanceGoalRepository;
import com.ems.hr.recruitment.RecruitmentCandidate;
import com.ems.hr.recruitment.RecruitmentService;
import com.ems.hr.reports.SimplePdfGenerator;
import com.ems.hr.settings.SettingsPayload;
import com.ems.hr.settings.SettingsService;
import com.ems.hr.training.TrainingCourse;
import com.ems.hr.training.TrainingService;
import com.ems.management.approvals.Approval;
import com.ems.management.approvals.ApprovalDTO;
import com.ems.management.approvals.ApprovalService;
import com.ems.management.meetings.Meeting;
import com.ems.management.meetings.MeetingDTO;
import com.ems.management.meetings.MeetingService;
import com.ems.management.performance_reviews.PerformanceReview;
import com.ems.management.performance_reviews.PerformanceReviewDTO;
import com.ems.management.performance_reviews.PerformanceReviewService;
import com.ems.management.projects.Project;
import com.ems.management.projects.ProjectDTO;
import com.ems.management.projects.ProjectService;
import com.ems.portal.InAppNotification;
import com.ems.portal.InAppNotificationRepository;
import com.ems.portal.NotificationService;
import com.ems.security.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;



// ==============================================================================================
// AUTHENTICATION
// ==============================================================================================

class AuthenticationControllers {


    // --------------------------------------------------------------------------------
    // AUTHENTICATION  (source: auth/controller/AuthController.java)
    // --------------------------------------------------------------------------------

	@RestController
	@RequestMapping("/api/auth")
	@Tag(name = "1. Authentication-Controller")
	static class AuthController {

	    private final AuthService authService;
	    private final JwtUtil     jwtUtil;

	    public AuthController(AuthService authService, JwtUtil jwtUtil) {
	        this.authService = authService;
	        this.jwtUtil     = jwtUtil;
	    }

	    @PostMapping("/login")
	    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
	        LoginResponse response = authService.login(request);
	        if (!response.isSuccess()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	        }
	        return ResponseEntity.ok(response);
	    }

	    @PostMapping("/logout")
	    public ResponseEntity<Map<String, String>> logout(
	            @RequestHeader(value = "Authorization", required = false) String authHeader) {
	        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
	    }

	    @PostMapping("/change-password")
	    public ResponseEntity<LoginResponse> changePassword(
	            @RequestHeader("Authorization") String authHeader,
	            @RequestBody ChangePasswordRequest request) {

	        String token = authHeader.replace("Bearer ", "").trim();

	        if (!jwtUtil.isValid(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(new LoginResponse("Invalid or expired token.", null, false, null, null, null, null, null));
	        }

	        if (request.getNewPassword() != null
	                && !request.getNewPassword().equals(request.getConfirmPassword())) {
	            return ResponseEntity.badRequest()
	                    .body(new LoginResponse("Passwords do not match.", null, false, null, null, null, null, null));
	        }

	        String employeeCode = jwtUtil.getEmployeeCode(token);
	        LoginResponse response = authService.changePassword(
	                employeeCode,
	                request.getCurrentPassword(),
	                request.getNewPassword()
	        );

	        if (!response.isSuccess()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	        }

	        return ResponseEntity.ok(response);
	    }
	    
	    @PostMapping("/forgot-password")
	    public ResponseEntity<LoginResponse> forgotPassword(
	            @RequestBody ForgotPasswordRequest request) {

	        LoginResponse response = authService.forgotPassword(request);

	        if (!response.isSuccess()) {
	            return ResponseEntity.badRequest().body(response);
	        }

	        return ResponseEntity.ok(response);
	    }
	    
	    @PostMapping("/verify-otp")
	    public ResponseEntity<LoginResponse> verifyOtp(
	            @RequestBody VerifyOtpRequest request) {

	        LoginResponse response = authService.verifyOtp(request);

	        if (!response.isSuccess()) {
	            return ResponseEntity.badRequest().body(response);
	        }

	        return ResponseEntity.ok(response);
	    }
	    @PostMapping("/resend-otp")
	    public ResponseEntity<LoginResponse> resendOtp(
	            @RequestBody ForgotPasswordRequest request) {

	        LoginResponse response = authService.resendOtp(request);

	        if (!response.isSuccess()) {
	            return ResponseEntity.badRequest().body(response);
	        }

	        return ResponseEntity.ok(response);
	    }
	    @PostMapping("/reset-password")
	    public ResponseEntity<LoginResponse> resetPassword(
	            @RequestBody ResetPasswordRequest request) {

	        if (request.getNewPassword() != null
	                && !request.getNewPassword().equals(request.getConfirmPassword())) {

	            return ResponseEntity.badRequest().body(
	                    new LoginResponse(
	                            "Passwords do not match.",
	                            null,
	                            false,
	                            null,
	                            null,
	                            null,
	                            null,
	                            null
	                    )
	            );
	        }

	        LoginResponse response = authService.resetPassword(request);

	        if (!response.isSuccess()) {
	            return ResponseEntity.badRequest().body(response);
	        }

	        return ResponseEntity.ok(response);
	    }
	    @GetMapping("/me")
	    public ResponseEntity<Map<String, String>> me(
	            @RequestHeader("Authorization") String authHeader) {

	        String token = authHeader.replace("Bearer ", "").trim();

	        if (!jwtUtil.isValid(token)) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("error", "Invalid or expired token."));
	        }

	        return ResponseEntity.ok(Map.of(
	                "employeeCode", jwtUtil.getEmployeeCode(token),
	                "role",         jwtUtil.getRole(token)
	        ));
	    }
	}



// ==============================================================================================
// EMPLOYEE SELF-SERVICE
// ==============================================================================================

class EmployeeControllers {


    // --------------------------------------------------------------------------------
    // EMPLOYEE SELF-SERVICE  (source: employee/controller/EmployeeDashboardApiController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/employees/{employeeCode}")
    @Tag(name = "2. Employee-Controller")
    static class EmployeeDashboardApiController {

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

        @PostMapping(
        	    value = "/profile-photo",
        	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        	)
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



    // --------------------------------------------------------------------------------
    // EMPLOYEE PROFILE (LEGACY)  (source: employee/controller/EmployeeProfileController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/employee-profiles")
    @Tag(name = "2. Employee-Controller")
    static class EmployeeProfileController {

        @Autowired
        EmployeeProfileService service;

        @GetMapping("/{employeeCode}")
        public EmployeeProfile getProfile(
                @PathVariable String employeeCode
        ) {

            return service.getProfile(employeeCode);
        }

        @PostMapping
        public EmployeeProfile save(
                @RequestBody EmployeeProfile profile
        ) {

            return service.save(profile);
        }
    }


}


// ==============================================================================================
// HR MODULE
// ==============================================================================================

class HrControllers {


    // --------------------------------------------------------------------------------
    // HR - ASSET MANAGEMENT  (source: hr/assets/AssetController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/assets")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    static class AssetController {

        private final AssetService service;

        public AssetController(AssetService service) {
            this.service = service;
        }

        /**
         * GET /api/assets - List all assets with optional filters
         */
        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> getAllAssets(
                @RequestParam(required = false) String search,
                @RequestParam(required = false) String category,
                @RequestParam(required = false) String status) {
            return ResponseEntity.ok(service.getAllAssets(search, category, status));
        }

        /**
         * GET /api/assets/{id} - Get asset by ID
         */
        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> getAsset(@PathVariable Long id) {
            return ResponseEntity.ok(service.getAssetById(id));
        }

        /**
         * POST /api/assets - Create new asset
         * Body: { "assetName": "...", "category": "...", "purchaseDate": "...", "cost": 0, "status": "..." }
         */
        @PostMapping
        public ResponseEntity<Map<String, Object>> createAsset(@RequestBody Map<String, Object> body) {
            Asset asset = new Asset();
            asset.setAssetName((String) body.get("assetName"));
            asset.setCategory((String) body.get("category"));
            asset.setAssetTag((String) body.get("assetTag"));
        
            // Parse purchaseDate
            if (body.get("purchaseDate") != null) {
                asset.setPurchaseDate(java.time.LocalDate.parse(body.get("purchaseDate").toString()));
            }
        
            // Parse cost
            if (body.get("cost") != null) {
                asset.setCost(new java.math.BigDecimal(body.get("cost").toString()));
            }
        
            // Parse status
            if (body.get("status") != null) {
                asset.setStatus(Asset.AssetStatus.valueOf(body.get("status").toString()));
            }
        
            // Parse currencyCode
            if (body.get("currencyCode") != null) {
                asset.setCurrencyCode(body.get("currencyCode").toString());
            }

            Asset created = service.createAsset(asset);
            Map<String, Object> result = service.getAssetById(created.getId());
            return ResponseEntity.ok(result);
        }

        /**
         * PUT /api/assets/{id} - Update asset
         */
        @PutMapping("/{id}")
        public ResponseEntity<Map<String, Object>> updateAsset(
                @PathVariable Long id,
                @RequestBody Map<String, Object> body) {
            Asset asset = new Asset();
            asset.setAssetName((String) body.get("assetName"));
            asset.setCategory((String) body.get("category"));
        
            if (body.get("purchaseDate") != null) {
                asset.setPurchaseDate(java.time.LocalDate.parse(body.get("purchaseDate").toString()));
            }
        
            if (body.get("cost") != null) {
                asset.setCost(new java.math.BigDecimal(body.get("cost").toString()));
            }
        
            if (body.get("status") != null) {
                asset.setStatus(Asset.AssetStatus.valueOf(body.get("status").toString()));
            }
        
            if (body.get("currencyCode") != null) {
                asset.setCurrencyCode(body.get("currencyCode").toString());
            }

            Asset updated = service.updateAsset(id, asset);
            Map<String, Object> result = service.getAssetById(updated.getId());
            return ResponseEntity.ok(result);
        }

        /**
         * DELETE /api/assets/{id} - Delete asset
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
            service.deleteAsset(id);
            return ResponseEntity.noContent().build();
        }

        /**
         * PUT /api/assets/{id}/assign - Assign asset to employee
         * Body: { "employeeCode": "..." }
         */
        @PutMapping("/{id}/assign")
        public ResponseEntity<Map<String, Object>> assignAsset(
                @PathVariable Long id,
                @RequestBody Map<String, String> body) {
            String employeeCode = body.get("employeeCode");
            if (employeeCode == null || employeeCode.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "employeeCode is required"));
            }
            return ResponseEntity.ok(service.assignAsset(id, employeeCode));
        }

        /**
         * PUT /api/assets/{id}/unassign - Unassign asset from employee
         */
        @PutMapping("/{id}/unassign")
        public ResponseEntity<Map<String, Object>> unassignAsset(@PathVariable Long id) {
            return ResponseEntity.ok(service.unassignAsset(id));
        }

        /**
         * GET /api/assets/stats - Get asset statistics
         */
        @GetMapping("/stats")
        public ResponseEntity<Map<String, Object>> getStatistics() {
            return ResponseEntity.ok(service.getAssetStatistics());
        }
    }



    // --------------------------------------------------------------------------------
    // HR - ATTENDANCE MANAGEMENT  (source: hr/attendance/AttendanceController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/attendance")
    @Tag(name = "3. HR-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
    static class AttendanceController {

        private final AttendanceService service;

        public AttendanceController(AttendanceService service) {
            this.service = service;
        }

        @GetMapping
        public ResponseEntity<List<AttendanceRecord>> getAll() {
            return ResponseEntity.ok(service.getAll());
        }

        @GetMapping("/statistics")
        public ResponseEntity<Map<String, Object>> statistics(
                @RequestParam(required = false) String fromDate,
                @RequestParam(required = false) String toDate,
                @RequestParam(required = false) String department,
                @RequestParam(required = false) String employeeCode,
                @RequestParam(required = false) String employeeId) {
            String employeeFilter = (employeeCode == null || employeeCode.isBlank()) ? employeeId : employeeCode;
            return ResponseEntity.ok(service.getStatistics(fromDate, toDate, department, employeeFilter));
        }

        @PostMapping("/mark")
        public ResponseEntity<String> markAttendance(@RequestBody Map<String, String> request) {
            return ResponseEntity.ok(service.markAttendance(request));
        }

        @PutMapping("/{id}/status")
        public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
            AttendanceRecord updated = service.updateStatus(String.valueOf(id), request.get("status"));
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        }

        @GetMapping("/date")
        public ResponseEntity<List<AttendanceRecord>> getByDate(@RequestParam String date) {
            return ResponseEntity.ok(service.getByDate(date));
        }

        @GetMapping("/employee/{id}")
        public ResponseEntity<List<AttendanceRecord>> getByEmployee(@PathVariable String id) {
            return ResponseEntity.ok(service.getByEmployee(id));
        }

        @GetMapping("/summary")
        public ResponseEntity<Map<String, Object>> summary() {
            List<AttendanceRecord> all = service.getAll();
            long present = all.stream().filter(a -> "Present".equalsIgnoreCase(a.getStatus())).count();
            long absent  = all.stream().filter(a -> "Absent".equalsIgnoreCase(a.getStatus())).count();
            long late    = all.stream().filter(a -> "Late".equalsIgnoreCase(a.getStatus())).count();
            long remote  = all.stream().filter(a -> "Remote".equalsIgnoreCase(a.getStatus())).count();
            long leave   = all.stream().filter(a -> "Leave".equalsIgnoreCase(a.getStatus())).count();
            long total   = all.size();
            String percentage = total > 0 ? Math.round(present * 100.0 / total) + "%" : "0%";
            return ResponseEntity.ok(Map.of(
                "present", present, "absent", absent, "late", late,
                "remote", remote, "leave", leave, "percentage", percentage
            ));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - ATTENDANCE CORRECTION  (source: hr/attendance/AttendanceCorrectionController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/attendance/corrections")
    @Tag(name = "3. HR-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
    static class AttendanceCorrectionController {

        private final AttendanceCorrectionService service;

        public AttendanceCorrectionController(AttendanceCorrectionService service) {
            this.service = service;
        }

        /**
         * GET /api/attendance/corrections
         * Returns all attendance regularization requests.
         */
        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> getAllCorrections() {
            return ResponseEntity.ok(service.getAllCorrections());
        }

        /**
         * PUT /api/attendance/corrections/{id}/approve
         * Approves a single correction request.
         */
        @PutMapping("/{id}/approve")
        public ResponseEntity<Map<String, Object>> approveCorrection(@PathVariable Long id) {
            Map<String, Object> result = service.approveCorrection(id);
            return ResponseEntity.ok(result);
        }

        /**
         * PUT /api/attendance/corrections/{id}/reject
         * Rejects a single correction request.
         */
        @PutMapping("/{id}/reject")
        public ResponseEntity<Map<String, Object>> rejectCorrection(@PathVariable Long id) {
            Map<String, Object> result = service.rejectCorrection(id);
            return ResponseEntity.ok(result);
        }

        /**
         * POST /api/attendance/corrections/bulk-approve
         * Approves multiple correction requests.
         * Body: { "ids": [1, 2, 3] }
         */
        @PostMapping("/bulk-approve")
        public ResponseEntity<Map<String, Object>> bulkApproveCorrections(@RequestBody Map<String, List<Long>> request) {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No IDs provided"));
            }
            int approvedCount = service.bulkApproveCorrections(ids);
            return ResponseEntity.ok(Map.of("approvedCount", approvedCount));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - DASHBOARD & REPORTS  (source: hr/dashboard/DashboardController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api")
    @Tag(name = "3. HR-Controller")
    static class DashboardController {

        private final DashboardService service;

        @PersistenceContext
        private EntityManager entityManager;

        public DashboardController(DashboardService service) {
            this.service = service;
        }

        @GetMapping("/dashboard/summary")
        public ResponseEntity<DashboardSummary> getSummary() {
            return ResponseEntity.ok(service.getSummary());
        }

        @GetMapping("/reports/attendance")
        public ResponseEntity<List<Map<String, Object>>> getAttendanceReport(@RequestParam(required = false) String department) {
            if (department != null && !department.isEmpty()) {
                return ResponseEntity.ok(queryAttendanceByDept(department));
            }
            return ResponseEntity.ok(queryAttendanceAll());
        }

        @GetMapping("/reports/leave")
        public ResponseEntity<List<Map<String, Object>>> getLeaveReport() {
            return ResponseEntity.ok(queryLeaveReport());
        }

        @GetMapping("/dashboard/attendance-statistics")
        public ResponseEntity<List<Map<String, Object>>> getAttendanceStatistics(@RequestParam(required = false) String department) {
            if (department != null && !department.isEmpty()) {
                return ResponseEntity.ok(queryAttendanceStatsByDept(department));
            }
            return ResponseEntity.ok(queryAttendanceStatsAll());
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> queryAttendanceAll() {
            List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT DATE_FORMAT(work_date, '%Y-%m') AS month, " +
                    "SUM(CASE WHEN status IN ('Present','Late') THEN 1 ELSE 0 END) AS presentCount, " +
                    "SUM(CASE WHEN status = 'Absent' THEN 1 ELSE 0 END) AS absentCount " +
                    "FROM attendance_records " +
                    "WHERE work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH) " +
                    "GROUP BY month ORDER BY month").getResultList();
            return mapRows(rows, "month", "presentCount", "absentCount");
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> queryAttendanceByDept(String department) {
            List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT DATE_FORMAT(ar.work_date, '%Y-%m') AS month, " +
                    "SUM(CASE WHEN ar.status IN ('Present','Late') THEN 1 ELSE 0 END) AS presentCount, " +
                    "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount " +
                    "FROM attendance_records ar " +
                    "JOIN employee_profiles ep ON ar.employee_code = ep.employee_code " +
                    "WHERE ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH) " +
                    "AND ep.department = ? " +
                    "GROUP BY month ORDER BY month")
                    .setParameter(1, department).getResultList();
            return mapRows(rows, "month", "presentCount", "absentCount");
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> queryLeaveReport() {
            List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT leave_type AS type, COUNT(*) AS count " +
                    "FROM leave_requests WHERE status = 'Approved' GROUP BY leave_type").getResultList();
            return mapRows(rows, "type", "count");
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> queryAttendanceStatsAll() {
            List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT ep.department, COUNT(DISTINCT ep.employee_code) AS headcount, " +
                    "SUM(CASE WHEN ar.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                    "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                    "SUM(CASE WHEN ar.status = 'Late' THEN 1 ELSE 0 END) AS lateCount " +
                    "FROM employee_profiles ep " +
                    "LEFT JOIN attendance_records ar ON ep.employee_code = ar.employee_code " +
                    "AND ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " +
                    "GROUP BY ep.department ORDER BY ep.department").getResultList();
            return mapRows(rows, "department", "headcount", "presentCount", "absentCount", "lateCount");
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> queryAttendanceStatsByDept(String department) {
            List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT ep.department, COUNT(DISTINCT ep.employee_code) AS headcount, " +
                    "SUM(CASE WHEN ar.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                    "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                    "SUM(CASE WHEN ar.status = 'Late' THEN 1 ELSE 0 END) AS lateCount " +
                    "FROM employee_profiles ep " +
                    "LEFT JOIN attendance_records ar ON ep.employee_code = ar.employee_code " +
                    "AND ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " +
                    "WHERE ep.department = ? " +
                    "GROUP BY ep.department ORDER BY ep.department")
                    .setParameter(1, department).getResultList();
            return mapRows(rows, "department", "headcount", "presentCount", "absentCount", "lateCount");
        }

        private List<Map<String, Object>> mapRows(List<Object[]> rows, String... labels) {
            List<Map<String, Object>> result = new java.util.ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < labels.length && i < row.length; i++) {
                    map.put(labels[i], row[i]);
                }
                result.add(map);
            }
            return result;
        }
    }



    // --------------------------------------------------------------------------------
    // HR - DASHBOARD METRICS  (source: hr/dashboard/DashboardMetricsController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/dashboard")
    @Tag(name = "3. HR-Controller")
    static class DashboardMetricsController {

        private final DashboardMetricsService service;

        public DashboardMetricsController(DashboardMetricsService service) {
            this.service = service;
        }

        @GetMapping("/metrics")
        public ResponseEntity<Map<String, Object>> getMetrics() {
            return ResponseEntity.ok(service.getMetrics());
        }

        @GetMapping("/audit-logs")
        public ResponseEntity<java.util.List<Map<String, Object>>> getAuditLogs() {
            return ResponseEntity.ok(service.getAuditLogs());
        }
    }



    // --------------------------------------------------------------------------------
    // HR - EMPLOYEE DIRECTORY  (source: hr/directory/DirectoryController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/employees")
    @Tag(name = "3. HR-Controller")
    static class DirectoryController {

        private final EmployeeStore store;

        public DirectoryController(EmployeeStore store) {
            this.store = store;
        }

        @GetMapping
        public ResponseEntity<List<Employee>> getAll() {
            return ResponseEntity.ok(store.getAll());
        }

        @GetMapping("/{id}")
        public ResponseEntity<Employee> getById(@PathVariable String id) {
            Employee employee = store.findById(id);
            if (employee == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(employee);
        }

        @GetMapping("/search")
        public ResponseEntity<List<Employee>> searchByName(@RequestParam String name) {
            List<Employee> result = store.getAll().stream()
                    .filter(e -> e.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }
        @GetMapping("/filter")
        public ResponseEntity<List<Employee>> filterByDepartment(@RequestParam String department) {
            List<Employee> result = store.getAll().stream()
                    .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        }

        @PostMapping
        public ResponseEntity<?> create(@RequestBody Employee employee) {
            if (employee.getId() == null || employee.getId().isBlank()) {
                employee.setId(store.nextId());
            }
            if (store.findById(employee.getId()) != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Employee already exists: " + employee.getId()));
            }
            store.add(employee);
            return ResponseEntity.ok(employee);
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> update(@PathVariable String id, @RequestBody Employee employee) {
            Employee existing = store.findById(id);
            if (existing == null) return ResponseEntity.notFound().build();

            employee.setId(id);
            store.replace(id, employee);
            return ResponseEntity.ok(employee);
        }


        @PutMapping("/{id}/activate")
        public ResponseEntity<?> activate(@PathVariable String id) {
            Employee employee = store.findById(id);
            if (employee == null) return ResponseEntity.notFound().build();

            employee.setStatus("ACTIVE");
            store.replace(id, employee);

            return ResponseEntity.ok(Map.of(
                    "message", "Employee activated",
                    "employeeId", id,
                    "status", employee.getStatus()
            ));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable String id) {
            boolean removed = store.delete(id);
            if (!removed) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(Map.of("message", "Employee deleted: " + id));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - DOCUMENT MANAGEMENT  (source: hr/documents/AdminDocumentController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/documents")
    @Tag(name = "3. HR-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    static class AdminDocumentController {

        private static final String DOCUMENT_TYPE = "documentType";
		private static final String EMPLOYEE_CODE = "employeeCode";
		private static final String DOCUMENT_NAME = "documentName";
		private static final String FILE2 = "file";
		private final AdminDocumentService service;

        public AdminDocumentController(AdminDocumentService service) {
            this.service = service;
        }

        /**
         * GET /api/documents - List all documents with optional filters
         */
        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> getAllDocuments(
                @RequestParam(required = false) String employeeCode,
                @RequestParam(required = false) String type,
                @RequestParam(required = false) String search) {
            return ResponseEntity.ok(service.getAllDocuments(employeeCode, type, search));
        }

        /**
         * POST /api/documents - Upload a new document
         * Multipart form data: file, documentType, employeeCode, documentName
         */
        @PostMapping(consumes = "multipart/form-data")
        public ResponseEntity<Map<String, Object>> uploadDocument(
                @RequestParam(FILE2) MultipartFile file,
                @RequestParam(DOCUMENT_TYPE) String documentType,
                @RequestParam(EMPLOYEE_CODE) String employeeCode,
                @RequestParam(DOCUMENT_NAME) String documentName,
                @RequestParam(required = false) String notes) {
        
            Map<String, Object> result = service.uploadDocument(file, documentType, employeeCode, documentName, notes);
            return ResponseEntity.ok(result);
        }

        /**
         * DELETE /api/documents/{id} - Delete a document
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
            service.deleteDocument(id);
            return ResponseEntity.noContent().build();
        }

        /**
         * GET /api/documents/stats - Get document statistics
         */
        @GetMapping("/stats")
        public ResponseEntity<Map<String, Object>> getStatistics() {
            return ResponseEntity.ok(service.getDocumentStatistics());
        }
    }



    // --------------------------------------------------------------------------------
    // HR - LEAVE MANAGEMENT  (source: hr/leave/LeaveService.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/leave")
    @Tag(name = "3. HR-Controller")
    static class LeaveController {

        private final LeaveService service;

        LeaveController(LeaveService service) { this.service = service; }

        /**
         * POST /api/leave/apply
         * Body: { "employeeId":"E002", "leaveType":"Sick",
         *         "fromDate":"2024-06-05", "toDate":"2024-06-05", "reason":"..." }
         */
        @PostMapping("/apply")
        @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
        public ResponseEntity<String> apply(@RequestBody Map<String, String> body) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean privileged = authentication != null && authentication.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority) || "ROLE_HR".equals(authority));
            String employeeId = body.get("employeeId");
            if (!privileged && authentication != null && !authentication.getName().equalsIgnoreCase(employeeId)) {
                return ResponseEntity.status(403).body("You can only submit leave for your own account.");
            }

            String result = service.applyLeave(body);
            if (result.startsWith("Leave type")
                    || result.startsWith("Please provide")
                    || result.startsWith("End date cannot")
                    || result.startsWith("Employee not found")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        }

        /**
         * PUT /api/leave/L002/approve
         * Admin/Manager clicks Approve button on the leave table row.
         */
        @PutMapping("/{id}/approve")
        @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
        public ResponseEntity<String> approve(@PathVariable String id) {
            return ResponseEntity.ok(service.approveLeave(id));
        }

        /**
         * PUT /api/leave/L002/reject
         * Admin/Manager clicks Reject button on the leave table row.
         */
        @PutMapping("/{id}/reject")
        @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
        public ResponseEntity<String> reject(@PathVariable String id) {
            return ResponseEntity.ok(service.rejectLeave(id));
        }

        /**
         * GET /api/leave
         * Loads the full leave table (ADMIN/HR), or team leaves (MANAGEMENT)
         */
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
        public ResponseEntity<List<LeaveRequest>> getAll() {
            return ResponseEntity.ok(service.getAll());
        }

        /**
         * GET /api/leave/requests
         * Management dashboard leave requests summary.
         */
        @GetMapping("/requests")
        @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
        public ResponseEntity<Map<String, Object>> getRequests() {
            List<LeaveRequest> all = service.getAll();
            List<Map<String, Object>> pending = new java.util.ArrayList<>();
            List<Map<String, Object>> approved = new java.util.ArrayList<>();
            for (LeaveRequest r : all) {
                Map<String, Object> m = new java.util.LinkedHashMap<>();
                m.put("name", r.getEmployeeName());
                m.put("role", "");
                m.put("initials", r.getEmployeeName() != null && !r.getEmployeeName().isBlank()
                    ? r.getEmployeeName().substring(0, 1).toUpperCase() : "?");
                m.put("bg", "bg-blue-100");
                m.put("text", "text-blue-600");
                if ("Pending".equalsIgnoreCase(r.getStatus())) {
                    m.put("type", r.getLeaveType());
                    m.put("dates", r.getFromDate() + " - " + r.getToDate());
                    m.put("reason", r.getReason());
                    pending.add(m);
                } else {
                    m.put("detail", r.getLeaveType() + " (" + r.getFromDate() + ")");
                    approved.add(m);
                }
            }
            List<Map<String, Object>> stats = List.of(
                Map.of("label", "Total Requests", "value", all.size(), "icon", "event_note", "bg", "bg-blue-50", "color", "text-blue-600"),
                Map.of("label", "Pending", "value", pending.size(), "icon", "pending_actions", "bg", "bg-amber-50", "color", "text-amber-600"),
                Map.of("label", "Approved", "value", approved.size(), "icon", "check_circle", "bg", "bg-emerald-50", "color", "text-emerald-600")
            );
            return ResponseEntity.ok(Map.of("pending", pending, "approved", approved, "stats", stats));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - LEAVE TYPES  (source: hr/leave/LeaveTypeController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/leave/types")
    @Tag(name = "3. HR-Controller")
    static class LeaveTypeController {

        private final LeaveTypeRepository repository;

        public LeaveTypeController(LeaveTypeRepository repository) {
            this.repository = repository;
        }

        @GetMapping
        public ResponseEntity<List<LeaveType>> getAll() {
            return ResponseEntity.ok(repository.findAll());
        }
    }



    // --------------------------------------------------------------------------------
    // HR - ONBOARDING  (source: hr/onboarding/OnboardingModule.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/onboarding")
    @Tag(name = "3. HR-Controller")
    static class OnboardingController {

        private final OnboardingService service;

        OnboardingController(OnboardingService service) {
            this.service = service;
        }

        @PostMapping("/step")
        public ResponseEntity<Map<String, Object>> step(@RequestBody Map<String, String> body) {
            return ResponseEntity.ok(service.saveStep(body));
        }

        @PostMapping("/complete")
        public ResponseEntity<Map<String, Object>> complete(@RequestBody Map<String, String> body) {
            return ResponseEntity.ok(service.complete(body.get("sessionId")));
        }

        @GetMapping("/{sessionId}")
        public ResponseEntity<?> getSession(@PathVariable String sessionId) {
            OnboardingSession session = service.getSession(sessionId);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(session);
        }

        @GetMapping("/recover/{employeeCode}")
        public ResponseEntity<?> recover(@PathVariable String employeeCode) {
            OnboardingSession session = service.recoverSession(employeeCode);
            if (session == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(session);
        }
    }



    // --------------------------------------------------------------------------------
    // HR - ONBOARDING TRACKING  (source: hr/onboarding/OnboardingTrackingController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/onboarding/tracking")
    @Tag(name = "3. HR-Controller")
    static class OnboardingTrackingController {

        private final OnboardingTrackingRepository repo;

        public OnboardingTrackingController(OnboardingTrackingRepository repo) {
            this.repo = repo;
        }

        @GetMapping
        public List<OnboardingTracking> list() {
            return repo.findAll();
        }

        @GetMapping("/search")
        public List<OnboardingTracking> search(@RequestParam String q) {
            return repo.search(q);
        }

        @GetMapping("/status/{status}")
        public List<OnboardingTracking> byStatus(@PathVariable String status) {
            return repo.findByStatus(status);
        }

        @GetMapping("/{id}")
        public ResponseEntity<OnboardingTracking> getOne(@PathVariable Long id) {
            return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping
        public ResponseEntity<OnboardingTracking> create(@RequestBody OnboardingTracking body) {
            body.recalcProgress();
            return ResponseEntity.ok(repo.save(body));
        }

        @PutMapping("/{id}")
        public ResponseEntity<OnboardingTracking> update(
                @PathVariable Long id, @RequestBody OnboardingTracking body) {
            return repo.findById(id).map(existing -> {
                existing.setEmployeeName(body.getEmployeeName());
                existing.setDepartment(body.getDepartment());
                existing.setJoiningDate(body.getJoiningDate());
                existing.setAssignedHr(body.getAssignedHr());
                existing.setNotes(body.getNotes());
                existing.setStep1Done(body.getStep1Done());
                existing.setStep2Done(body.getStep2Done());
                existing.setStep3Done(body.getStep3Done());
                existing.setStep4Done(body.getStep4Done());
                existing.setStep5Done(body.getStep5Done());
                existing.recalcProgress();
                return ResponseEntity.ok(repo.save(existing));
            }).orElse(ResponseEntity.notFound().build());
        }

        /** Mark a specific step (1–5) as done */
        @PutMapping("/{id}/step/{stepNum}")
        public ResponseEntity<OnboardingTracking> markStep(
                @PathVariable Long id, @PathVariable int stepNum) {
            return repo.findById(id).map(ob -> {
                switch (stepNum) {
                    case 1 -> ob.setStep1Done(true);
                    case 2 -> ob.setStep2Done(true);
                    case 3 -> ob.setStep3Done(true);
                    case 4 -> ob.setStep4Done(true);
                    case 5 -> ob.setStep5Done(true);
                }
                ob.recalcProgress();
                return ResponseEntity.ok(repo.save(ob));
            }).orElse(ResponseEntity.notFound().build());
        }

        /** Mark all steps done → Completed */
        @PutMapping("/{id}/complete")
        public ResponseEntity<OnboardingTracking> complete(@PathVariable Long id) {
            return repo.findById(id).map(ob -> {
                ob.setStep1Done(true); ob.setStep2Done(true); ob.setStep3Done(true);
                ob.setStep4Done(true); ob.setStep5Done(true);
                ob.setStatus("Completed");
                ob.recalcProgress();
                return ResponseEntity.ok(repo.save(ob));
            }).orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
            if (!repo.existsById(id)) return ResponseEntity.notFound().build();
            repo.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Onboarding record deleted."));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - PAYROLL  (source: hr/payroll/PayrollModule.java)
    // --------------------------------------------------------------------------------

    @RestController
    @Profile("all-controllers-payroll")
    @RequestMapping("/api/payroll")
    @Tag(name = "3. HR-Controller")
    static class PayrollController {

        private final PayrollService service;

        PayrollController(PayrollService service) {
            this.service = service;
        }

        @GetMapping
        public ResponseEntity<Object> getAll(@RequestParam(required = false) String month) {
            return ResponseEntity.ok(service.getAllPayslips(month));
        }

        @GetMapping("/summary")
        public ResponseEntity<Map<String, Object>> summary(@RequestParam(required = false) String month) {
            return ResponseEntity.ok(service.getSummary(month));
        }

        @GetMapping("/{employeeId}")
        public ResponseEntity<?> getOne(
                @PathVariable String employeeId,
                @RequestParam(required = false) String month) {
            PayslipDTO payslip = service.getPayslip(employeeId, month);
            if (payslip == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(payslip);
        }

        @GetMapping("/{employeeId}/pdf")
        public void getPdf(
                @PathVariable String employeeId,
                @RequestParam(required = false) String month,
                jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
            PayslipDTO p = service.getPayslip(employeeId, month);
            if (p == null) {
                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Payslip not found");
                return;
            }
        
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"payslip_" + employeeId + "_" + p.getMonth().replace(" ", "_") + ".pdf\"");
        
            List<String> headers = java.util.Arrays.asList("Payroll Component", "Amount / Detail");
            List<List<String>> rows = new java.util.ArrayList<>();
            rows.add(java.util.Arrays.asList("Company", "Enterprise Management Systems Pvt. Ltd."));
            rows.add(java.util.Arrays.asList("Employee Name", p.getEmployeeName()));
            rows.add(java.util.Arrays.asList("Employee Code", p.getEmployeeId()));
            rows.add(java.util.Arrays.asList("Department", p.getDepartment()));
            rows.add(java.util.Arrays.asList("Designation", p.getDesignation()));
            rows.add(java.util.Arrays.asList("Month", p.getMonth()));
            rows.add(java.util.Arrays.asList("Basic Salary", String.format("%.2f %s", p.getBasicSalary(), p.getCurrencyCode())));
            rows.add(java.util.Arrays.asList("HRA", String.format("%.2f %s", p.getHra(), p.getCurrencyCode())));
            rows.add(java.util.Arrays.asList("Allowances", String.format("%.2f %s", p.getAllowances(), p.getCurrencyCode())));
            rows.add(java.util.Arrays.asList("Deductions", String.format("%.2f %s", p.getDeductions(), p.getCurrencyCode())));
            rows.add(java.util.Arrays.asList("Net Salary", String.format("%.2f %s", p.getNetSalary(), p.getCurrencyCode())));
        
            byte[] pdfBytes = com.ems.hr.reports.SimplePdfGenerator.generatePdf("Enterprise Management Systems — Payslip", headers, rows);
            response.getOutputStream().write(pdfBytes);
        }

        @GetMapping("/employee/{employeeId}")
        public ResponseEntity<List<PayslipDTO>> getEmployeePayslips(
                @PathVariable String employeeId) {
            return ResponseEntity.ok(service.getEmployeePayslips(employeeId));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - RECRUITMENT  (source: hr/recruitment/RecruitmentModule.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/recruitment")
    @Tag(name = "3. HR-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    static class RecruitmentController {
        private final RecruitmentService service;

        RecruitmentController(RecruitmentService service) {
            this.service = service;
        }

        @GetMapping
        public ResponseEntity<List<RecruitmentCandidate>> getAll() {
            return ResponseEntity.ok(service.getAll());
        }

        @PostMapping
        public ResponseEntity<RecruitmentCandidate> add(@RequestBody @jakarta.validation.Valid RecruitmentCandidate candidate) {
            return ResponseEntity.ok(service.addCandidate(candidate));
        }

        @PutMapping("/{id}/status")
        public ResponseEntity<RecruitmentCandidate> updateStatus(
                @PathVariable String id,
                @RequestParam(required = false) String status,
                @RequestBody(required = false) Map<String, String> body) {
            String nextStatus = status != null ? status : (body != null ? body.get("status") : null);
            if (nextStatus == null || nextStatus.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            RecruitmentCandidate updated = service.updateStatus(id, normalizeStatus(nextStatus));
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        }

        private String normalizeStatus(String status) {
            String normalized = status.trim().toLowerCase().replace('_', ' ');
            return java.util.Arrays.stream(normalized.split("\\s+"))
                    .filter(part -> !part.isBlank())
                    .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1))
                    .collect(java.util.stream.Collectors.joining(" "));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - REPORTS  (source: hr/reports/ReportsModule.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/reports")
    @Tag(name = "3. HR-Controller")
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    static class ReportsModule {

        private final EntityManager em;

        public ReportsModule(EntityManager em) {
            this.em = em;
        }

        @GetMapping
        public ResponseEntity<Map<String, Object>> getReportsOverview() {
            List<Map<String, Object>> general = new ArrayList<>();
            List<Map<String, Object>> department = new ArrayList<>();
            List<Map<String, Object>> analytics = new ArrayList<>();
            List<Map<String, Object>> employeeData = getEmployeeReport();
            List<Map<String, Object>> deptData = getDepartmentReport();
            general.add(Map.of("id", 1, "title", "Employee Directory", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Completed"));
            general.add(Map.of("id", 2, "title", "Payroll Summary", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Completed"));
            general.add(Map.of("id", 3, "title", "Attendance Overview", "date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), "status", "Pending"));
            for (Map<String, Object> d : deptData) {
                department.add(Map.of("id", d.get("dept_code"), "department", d.get("dept_name"), "employees", d.get("employee_count"), "avgRating", "-"));
            }
            analytics.add(Map.of("metric", "Total Employees", "value", employeeData.size(), "change", "+" + Math.round(employeeData.size() * 0.05)));
            analytics.add(Map.of("metric", "Active Departments", "value", deptData.size(), "change", "0"));
            return ResponseEntity.ok(Map.of("general", general, "department", department, "analytics", analytics));
        }

        @GetMapping("/employee")
        public List<Map<String, Object>> getEmployeeReport() {
            return queryForList(
                    "SELECT employee_code, full_name, email, phone_number, department, designation, date_of_joining, salary, status, gender FROM employee_profiles ORDER BY employee_code ASC",
                    "employee_code", "full_name", "email", "phone_number", "department", "designation", "date_of_joining", "salary", "status", "gender"
            );
        }

        @GetMapping("/department")
        public List<Map<String, Object>> getDepartmentReport() {
            return queryForList(
                    "SELECT d.dept_code, d.dept_name, d.head_employee_code, (SELECT COUNT(*) FROM employee_profiles ep WHERE ep.department = d.dept_name) as employee_count FROM department_master d ORDER BY d.dept_code ASC",
                    "dept_code", "dept_name", "head_employee_code", "employee_count"
            );
        }

        @GetMapping("/recruitment")
        public List<Map<String, Object>> getRecruitmentReport() {
            return queryForList(
                    "SELECT id, full_name, email, phone, source, created_at FROM candidates ORDER BY id DESC",
                    "id", "full_name", "email", "phone", "source", "created_at"
            );
        }

        @GetMapping("/payroll")
        public List<Map<String, Object>> getPayrollReport() {
            return queryForList(
                    "SELECT employee_code, payroll_month, basic_salary, hra, allowances, pf_deduction, tax_deduction, other_deductions, net_salary, currency_code, status FROM payslips ORDER BY payroll_month DESC, employee_code ASC",
                    "employee_code", "payroll_month", "basic_salary", "hra", "allowances", "pf_deduction", "tax_deduction", "other_deductions", "net_salary", "currency_code", "status"
            );
        }

        @GetMapping("/export/{reportType}/{format}")
        public void exportReport(
                @PathVariable String reportType,
                @PathVariable String format,
                HttpServletResponse response) throws IOException {

            System.out.println("Exporting " + reportType + " report as " + format);

            List<String> headers = new ArrayList<>();
            List<List<String>> data = new ArrayList<>();
            String title = "";

            if ("employee".equalsIgnoreCase(reportType)) {
                title = "Employee Directory Report";
                headers = Arrays.asList("Code", "Name", "Email", "Phone", "Department", "Designation", "Joining Date", "Salary", "Status", "Gender");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT employee_code, full_name, email, phone_number, department, designation, date_of_joining, salary, status, gender FROM employee_profiles"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7]), str(r[8]), str(r[9])));
                }
            } else if ("department".equalsIgnoreCase(reportType)) {
                title = "Department Master Report";
                headers = Arrays.asList("Dept Code", "Dept Name", "Head Code", "Employees Count");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT d.dept_code, d.dept_name, d.head_employee_code, (SELECT COUNT(*) FROM employee_profiles ep WHERE ep.department = d.dept_name) as emp_count FROM department_master d"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3])));
                }
            } else if ("recruitment".equalsIgnoreCase(reportType)) {
                title = "Recruitment Funnel Report";
                headers = Arrays.asList("ID", "Candidate Name", "Email", "Phone", "Source");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT id, full_name, email, phone, source FROM candidates"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4])));
                }
            } else if ("payroll".equalsIgnoreCase(reportType)) {
                title = "Payroll Run Report";
                headers = Arrays.asList("Emp Code", "Month", "Basic", "HRA", "Allowances", "PF Ded.", "Tax Ded.", "Net Salary", "Status");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT employee_code, payroll_month, basic_salary, hra, allowances, pf_deduction, tax_deduction, net_salary, status FROM payslips"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7]), str(r[8])));
                }
            } else if ("attendance".equalsIgnoreCase(reportType)) {
                title = "Attendance Summary Report";
                headers = Arrays.asList("Emp Code", "Work Date", "Check In", "Check Out", "Status", "Hours Worked");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT employee_code, work_date, check_in, check_out, status, hours_worked FROM attendance_records"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5])));
                }
            } else if ("leave".equalsIgnoreCase(reportType)) {
                title = "Leave Request Report";
                headers = Arrays.asList("ID", "Emp Code", "Emp Name", "Leave Type", "Start Date", "End Date", "Days", "Status");
                List<Object[]> rows = em.createNativeQuery(
                        "SELECT id, employee_code, employee_name, leave_type, start_date, end_date, number_of_days, status FROM leave_requests"
                ).getResultList();
                for (Object[] r : rows) {
                    data.add(Arrays.asList(str(r[0]), str(r[1]), str(r[2]), str(r[3]), str(r[4]), str(r[5]), str(r[6]), str(r[7])));
                }
            } else {
                title = "System Report";
                headers = Arrays.asList("Message");
                data.add(Collections.singletonList("Report type " + reportType + " not recognized."));
            }

            if ("csv".equalsIgnoreCase(format)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.csv\"");
                writeCsv(headers, data, response.getOutputStream());
            } else if ("excel".equalsIgnoreCase(format)) {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.xls\"");
                writeTsv(headers, data, response.getOutputStream());
            } else if ("pdf".equalsIgnoreCase(format)) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + "_report.pdf\"");
                byte[] pdfBytes = SimplePdfGenerator.generatePdf(title, headers, data);
                response.getOutputStream().write(pdfBytes);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Unsupported format: " + format);
            }
        }

        private List<Map<String, Object>> queryForList(String sql, String... columns) {
            List<Object[]> rows = em.createNativeQuery(sql).getResultList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Object[] row : rows) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < columns.length && i < row.length; i++) {
                    map.put(columns[i], row[i]);
                }
                result.add(map);
            }
            return result;
        }

        private String str(Object val) {
            if (val == null) return "";
            return val.toString();
        }

        private void writeCsv(List<String> headers, List<List<String>> rows, OutputStream os) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                sb.append(escapeCsv(headers.get(i)));
                if (i < headers.size() - 1) sb.append(",");
            }
            sb.append("\n");
            for (List<String> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    sb.append(escapeCsv(row.get(i)));
                    if (i < row.size() - 1) sb.append(",");
                }
                sb.append("\n");
            }
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        private String escapeCsv(String val) {
            if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
                return "\"" + val.replace("\"", "\"\"") + "\"";
            }
            return val;
        }

        private void writeTsv(List<String> headers, List<List<String>> rows, OutputStream os) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                sb.append(headers.get(i));
                if (i < headers.size() - 1) sb.append("\t");
            }
            sb.append("\n");
            for (List<String> row : rows) {
                for (int i = 0; i < row.size(); i++) {
                    sb.append(row.get(i));
                    if (i < row.size() - 1) sb.append("\t");
                }
                sb.append("\n");
            }
            os.write(sb.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }



    // --------------------------------------------------------------------------------
    // HR - SETTINGS  (source: hr/settings/SettingsController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/hr/settings")
    @Tag(name = "3. HR-Controller")
    static class SettingsController {

        private final SettingsService service;

        public SettingsController(SettingsService service) {
            this.service = service;
        }

        @GetMapping
        public ResponseEntity<Map<String, Object>> getSettings() {
            return ResponseEntity.ok(service.grouped());
        }

        @PutMapping
        public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody SettingsPayload payload) {
            return ResponseEntity.ok(service.save(payload));
        }

        @PostMapping(value = "/company-logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<Map<String, String>> uploadLogo(@RequestParam("file") MultipartFile file) throws IOException {
            return ResponseEntity.ok(Map.of("companyLogoUrl", service.updateLogo(file)));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - TRAINING  (source: hr/training/TrainingController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/training")
    @Tag(name = "3. HR-Controller")
    static class TrainingController {

        private final TrainingService trainingService;

        public TrainingController(TrainingService trainingService) {
            this.trainingService = trainingService;
        }

        @GetMapping("/courses")
        public ResponseEntity<List<TrainingCourse>> getAllCourses() {
            return ResponseEntity.ok(trainingService.getAllCourses());
        }

        @GetMapping("/courses/all")
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        public ResponseEntity<List<TrainingCourse>> getAllCoursesForAdmin() {
            return ResponseEntity.ok(trainingService.getAllCoursesForAdmin());
        }

        /**
         * POST /api/training/courses
         * Creates a new training course
         */
        @PostMapping("/courses")
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Map<String, Object> request) {
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String category = (String) request.get("category");
            Integer durationHours = request.get("durationHours") != null 
                ? Integer.valueOf(request.get("durationHours").toString()) 
                : null;
            String instructor = (String) request.get("instructor");
            String status = (String) request.get("status");
        
            return ResponseEntity.ok(trainingService.createCourse(title, description, category, durationHours, instructor, status));
        }

        /**
         * PUT /api/training/courses/{id}
         * Updates a training course
         */
        @PutMapping("/courses/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        public ResponseEntity<Map<String, Object>> updateCourse(
                @PathVariable Long id,
                @RequestBody Map<String, Object> request) {
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            String category = (String) request.get("category");
            Integer durationHours = request.get("durationHours") != null 
                ? Integer.valueOf(request.get("durationHours").toString()) 
                : null;
            String instructor = (String) request.get("instructor");
            String status = (String) request.get("status");
        
            return ResponseEntity.ok(trainingService.updateCourse(id, title, description, category, durationHours, instructor, status));
        }

        /**
         * DELETE /api/training/courses/{id}
         * Archives a training course (soft delete)
         */
        @DeleteMapping("/courses/{id}")
        @PreAuthorize("hasAnyRole('ADMIN','HR')")
        public ResponseEntity<Map<String, Object>> archiveCourse(@PathVariable Long id) {
            return ResponseEntity.ok(trainingService.archiveCourse(id));
        }

        /**
         * GET /api/training/enrollments/{employeeCode}
         * Returns enrollments for an employee
         */
        @GetMapping("/enrollments/{employeeCode}")
        public ResponseEntity<List<Map<String, Object>>> getEnrollments(@PathVariable String employeeCode) {
            return ResponseEntity.ok(trainingService.getEnrollments(employeeCode));
        }

        /**
         * POST /api/training/enroll
         * Enrolls an employee in a course
         */
        @PostMapping("/enroll")
        public ResponseEntity<Map<String, Object>> enrollEmployee(@RequestBody Map<String, Object> request) {
            Long courseId = request.get("courseId") != null 
                ? Long.valueOf(request.get("courseId").toString()) 
                : null;
            String employeeCode = (String) request.get("employeeCode");
        
            if (courseId == null || employeeCode == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "courseId and employeeCode are required"));
            }
        
            return ResponseEntity.ok(trainingService.enrollEmployee(courseId, employeeCode));
        }

        /**
         * PUT /api/training/enrollments/{enrollmentId}/progress
         * Updates progress for an enrollment
         */
        @PutMapping("/enrollments/{enrollmentId}/progress")
        public ResponseEntity<Map<String, Object>> updateProgress(
                @PathVariable Long enrollmentId,
                @RequestBody Map<String, Object> request) {
            Integer progress = request.get("progress") != null 
                ? Integer.valueOf(request.get("progress").toString()) 
                : 0;
        
            return ResponseEntity.ok(trainingService.updateProgress(enrollmentId, progress));
        }
    }



    // --------------------------------------------------------------------------------
    // HR - USER STATUS  (source: hr/userstatus/UserStatusController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/users")
    @Tag(name = "3. HR-Controller")
    static class UserStatusController {

        private final UserRepository userRepository;

        public UserStatusController(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> listUsers() {
            List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> Map.<String, Object>of(
                    "employeeCode", u.getEmployeeCode() != null ? u.getEmployeeCode() : "",
                    "email",        u.getUsername(),
                    "role",         u.getRole(),
                    "status",       Boolean.TRUE.equals(u.getIsAlive()) ? "Active" : "Inactive"
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(users);
        }

        @PutMapping("/{employeeCode}/activate")
        public ResponseEntity<Map<String, Object>> activate(@PathVariable String employeeCode) {
            return toggleStatus(employeeCode, true);
        }

        @PutMapping("/{employeeCode}/deactivate")
        public ResponseEntity<Map<String, Object>> deactivate(@PathVariable String employeeCode) {
            return toggleStatus(employeeCode, false);
        }

        @GetMapping("/{employeeCode}/status")
        public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String employeeCode) {
            Optional<User> opt = userRepository.findByEmployeeCode(employeeCode);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();
            User u = opt.get();
            return ResponseEntity.ok(Map.of(
                "employeeCode", u.getEmployeeCode(),
                "email",        u.getUsername(),
                "role",         u.getRole(),
                "status",       Boolean.TRUE.equals(u.getIsAlive()) ? "Active" : "Inactive",
                "isAlive",      Boolean.TRUE.equals(u.getIsAlive())
            ));
        }

        private ResponseEntity<Map<String, Object>> toggleStatus(String code, boolean active) {
            Optional<User> opt = userRepository.findByEmployeeCode(code);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();
            User user = opt.get();
            user.setIsAlive(active);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of(
                "employeeCode", user.getEmployeeCode(),
                "status",       active ? "Active" : "Inactive",
                "message",      "Account " + (active ? "activated" : "deactivated") + " successfully."
            ));
        }
    }


}


// ==============================================================================================
// MANAGEMENT MODULE
// ==============================================================================================

class ManagementControllers {


    // --------------------------------------------------------------------------------
    // MANAGEMENT - ATTENDANCE OVERVIEW  (source: hr/attendance/ManagementAttendanceController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/management/attendance")
    @Tag(name = "4. Management-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
    static class ManagementAttendanceController {

        private final ManagementAttendanceService service;

        public ManagementAttendanceController(ManagementAttendanceService service) {
            this.service = service;
        }

        /**
         * GET /api/management/attendance/dashboard
         * Returns today's attendance dashboard metrics.
         */
        @GetMapping("/dashboard")
        public ResponseEntity<Map<String, Object>> getDashboard() {
            return ResponseEntity.ok(service.getDashboardMetrics());
        }

        /**
         * GET /api/management/attendance
         * Returns paginated attendance records with optional filters.
         */
        @GetMapping
        public ResponseEntity<Map<String, Object>> getAttendanceRecords(
                @RequestParam(defaultValue = "1") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String search,
                @RequestParam(required = false) String department,
                @RequestParam(required = false) String status,
                @RequestParam(required = false) String date,
                @RequestParam(required = false) String team) {
            return ResponseEntity.ok(service.getAttendanceRecords(page, size, search, department, status, date, team));
        }

        /**
         * GET /api/management/attendance/trends?days=7
         * Returns attendance trends for the last N days.
         */
        @GetMapping("/trends")
        public ResponseEntity<List<Map<String, Object>>> getAttendanceTrends(
                @RequestParam(defaultValue = "7") int days) {
            return ResponseEntity.ok(service.getAttendanceTrends(days));
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - APPROVALS  (source: management/approvals/ApprovalController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/approvals")
    @Tag(name = "4. Management-Controller")
    static class ApprovalController {
    
        private final ApprovalService approvalService;
    
        public ApprovalController(ApprovalService approvalService) {
            this.approvalService = approvalService;
        }
    
        /**
         * GET /api/approvals
         * Returns all approvals
         */
        @GetMapping
        public ResponseEntity<List<Approval>> getAllApprovals() {
            return ResponseEntity.ok(approvalService.getAllApprovals());
        }
    
        /**
         * GET /api/approvals/{id}
         * Returns approval by ID
         */
        @GetMapping("/{id}")
        public ResponseEntity<Approval> getApprovalById(@PathVariable Long id) {
            return ResponseEntity.ok(approvalService.getApprovalById(id));
        }
    
        /**
         * POST /api/approvals
         * Creates a new approval request
         */
        @PostMapping
        public ResponseEntity<Approval> createApproval(@RequestBody ApprovalDTO approvalDTO) {
            Approval approval = approvalService.createApproval(approvalDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(approval);
        }
    
        /**
         * GET /api/approvals/status/{status}
         * Returns approvals by status
         */
        @GetMapping("/status/{status}")
        public ResponseEntity<List<Approval>> getApprovalsByStatus(@PathVariable String status) {
            return ResponseEntity.ok(approvalService.getApprovalsByStatus(status));
        }
    
        /**
         * GET /api/approvals/pending
         * Returns all pending approvals
         */
        @GetMapping("/pending")
        public ResponseEntity<List<Approval>> getPendingApprovals() {
            return ResponseEntity.ok(approvalService.getPendingApprovals());
        }
    
        /**
         * GET /api/approvals/pending/{approver}
         * Returns pending approvals for specific approver
         */
        @GetMapping("/pending/{approver}")
        public ResponseEntity<List<Approval>> getPendingApprovalsForUser(@PathVariable String approver) {
            return ResponseEntity.ok(approvalService.getPendingApprovalsForUser(approver));
        }
    
        /**
         * GET /api/approvals/requested-by/{requestedBy}
         * Returns approvals by who requested them
         */
        @GetMapping("/requested-by/{requestedBy}")
        public ResponseEntity<List<Approval>> getApprovalsByRequestedBy(@PathVariable String requestedBy) {
            return ResponseEntity.ok(approvalService.getApprovalsByRequestedBy(requestedBy));
        }
    
        /**
         * GET /api/approvals/approver/{approver}
         * Returns approvals for a specific approver
         */
        @GetMapping("/approver/{approver}")
        public ResponseEntity<List<Approval>> getApprovalsByApprover(@PathVariable String approver) {
            return ResponseEntity.ok(approvalService.getApprovalsByApprover(approver));
        }
    
        /**
         * PUT /api/approvals/{id}/approve
         * Approves a request
         */
        @PutMapping("/{id}/approve")
        public ResponseEntity<Map<String, Object>> approveRequest(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String comments = request.getOrDefault("comments", "");
            Approval approval = approvalService.approveRequest(id, comments);
        
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Approval request approved successfully");
            response.put("approval", approval);
            return ResponseEntity.ok(response);
        }
    
        /**
         * PUT /api/approvals/{id}/reject
         * Rejects a request
         */
        @PutMapping("/{id}/reject")
        public ResponseEntity<Map<String, Object>> rejectRequest(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String comments = request.getOrDefault("comments", "Rejected");
            Approval approval = approvalService.rejectRequest(id, comments);
        
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Approval request rejected");
            response.put("approval", approval);
            return ResponseEntity.ok(response);
        }
    
        /**
         * PUT /api/approvals/{id}
         * Updates an approval
         */
        @PutMapping("/{id}")
        public ResponseEntity<Approval> updateApproval(
                @PathVariable Long id,
                @RequestBody ApprovalDTO approvalDTO) {
        
            Approval approval = approvalService.updateApproval(id, approvalDTO);
            return ResponseEntity.ok(approval);
        }
    
        /**
         * DELETE /api/approvals/{id}
         * Deletes an approval
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteApproval(@PathVariable Long id) {
            approvalService.deleteApproval(id);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Approval deleted successfully");
            return ResponseEntity.ok(response);
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - MEETINGS  (source: management/meetings/MeetingController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/meetings")
    @Tag(name = "4. Management-Controller")
    static class MeetingController {
    
        private final MeetingService meetingService;
    
        public MeetingController(MeetingService meetingService) {
            this.meetingService = meetingService;
        }
    
        /**
         * GET /api/meetings
         * Returns all meetings
         */
        @GetMapping
        public ResponseEntity<List<Meeting>> getAllMeetings() {
            return ResponseEntity.ok(meetingService.getAllMeetings());
        }
    
        /**
         * GET /api/meetings/{id}
         * Returns meeting by ID
         */
        @GetMapping("/{id}")
        public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
            return ResponseEntity.ok(meetingService.getMeetingById(id));
        }
    
        /**
         * POST /api/meetings
         * Creates a new meeting
         */
        @PostMapping
        public ResponseEntity<Meeting> createMeeting(@RequestBody MeetingDTO meetingDTO) {
            Meeting meeting = meetingService.createMeeting(meetingDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
        }
    
        /**
         * GET /api/meetings/status/{status}
         * Returns meetings by status
         */
        @GetMapping("/status/{status}")
        public ResponseEntity<List<Meeting>> getMeetingsByStatus(@PathVariable String status) {
            return ResponseEntity.ok(meetingService.getMeetingsByStatus(status));
        }
    
        /**
         * GET /api/meetings/scheduled
         * Returns all scheduled meetings
         */
        @GetMapping("/scheduled")
        public ResponseEntity<List<Meeting>> getScheduledMeetings() {
            return ResponseEntity.ok(meetingService.getScheduledMeetings());
        }
    
        /**
         * GET /api/meetings/organizer/{organizer}
         * Returns meetings organized by a user
         */
        @GetMapping("/organizer/{organizer}")
        public ResponseEntity<List<Meeting>> getMeetingsByOrganizer(@PathVariable String organizer) {
            return ResponseEntity.ok(meetingService.getMeetingsByOrganizer(organizer));
        }
    
        /**
         * GET /api/meetings/attendee/{attendee}
         * Returns meetings attended by a user
         */
        @GetMapping("/attendee/{attendee}")
        public ResponseEntity<List<Meeting>> getMeetingsByAttendee(@PathVariable String attendee) {
            return ResponseEntity.ok(meetingService.getMeetingsByAttendee(attendee));
        }
    
        /**
         * GET /api/meetings/between?startDate=yyyy-MM-ddThh:mm:ss&endDate=yyyy-MM-ddThh:mm:ss
         * Returns meetings between two dates
         */
        @GetMapping("/between")
        public ResponseEntity<List<Meeting>> getMeetingsBetweenDates(
                @RequestParam LocalDateTime startDate,
                @RequestParam LocalDateTime endDate) {
            return ResponseEntity.ok(meetingService.getMeetingsBetweenDates(startDate, endDate));
        }
    
        /**
         * PUT /api/meetings/{id}
         * Updates a meeting
         */
        @PutMapping("/{id}")
        public ResponseEntity<Meeting> updateMeeting(
                @PathVariable Long id,
                @RequestBody MeetingDTO meetingDTO) {
        
            Meeting meeting = meetingService.updateMeeting(id, meetingDTO);
            return ResponseEntity.ok(meeting);
        }
    
        /**
         * PUT /api/meetings/{id}/status
         * Updates meeting status
         */
        @PutMapping("/{id}/status")
        public ResponseEntity<Meeting> updateMeetingStatus(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String status = request.get("status");
            Meeting meeting = meetingService.updateMeetingStatus(id, status);
            return ResponseEntity.ok(meeting);
        }
    
        /**
         * POST /api/meetings/{id}/attendees
         * Adds an attendee to a meeting
         */
        @PostMapping("/{id}/attendees")
        public ResponseEntity<Map<String, String>> addAttendee(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String attendee = request.get("attendee");
            meetingService.addAttendee(id, attendee);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Attendee added successfully");
            return ResponseEntity.ok(response);
        }
    
        /**
         * DELETE /api/meetings/{id}/attendees/{attendee}
         * Removes an attendee from a meeting
         */
        @DeleteMapping("/{id}/attendees/{attendee}")
        public ResponseEntity<Map<String, String>> removeAttendee(
                @PathVariable Long id,
                @PathVariable String attendee) {
        
            meetingService.removeAttendee(id, attendee);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Attendee removed successfully");
            return ResponseEntity.ok(response);
        }
    
        /**
         * DELETE /api/meetings/{id}
         * Deletes a meeting
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteMeeting(@PathVariable Long id) {
            meetingService.deleteMeeting(id);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Meeting deleted successfully");
            return ResponseEntity.ok(response);
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - PERFORMANCE REVIEWS  (source: management/performance_reviews/PerformanceReviewController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/performance-reviews")
    @Tag(name = "4. Management-Controller")
    static class PerformanceReviewController {
    
        private final PerformanceReviewService performanceReviewService;
    
        public PerformanceReviewController(PerformanceReviewService performanceReviewService) {
            this.performanceReviewService = performanceReviewService;
        }
    
        @GetMapping
        public ResponseEntity<List<PerformanceReview>> getAllReviews() {
            return ResponseEntity.ok(performanceReviewService.getAllReviews());
        }
    
        @GetMapping("/{id}")
        public ResponseEntity<PerformanceReview> getReviewById(@PathVariable Long id) {
            return ResponseEntity.ok(performanceReviewService.getReviewById(id));
        }
    
        @PostMapping
        public ResponseEntity<PerformanceReview> createReview(@RequestBody PerformanceReviewDTO dto) {
            PerformanceReview review = performanceReviewService.createReview(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        }
    
        @GetMapping("/employee/{employeeId}")
        public ResponseEntity<List<PerformanceReview>> getReviewsByEmployeeId(@PathVariable String employeeId) {
            return ResponseEntity.ok(performanceReviewService.getReviewsByEmployeeId(employeeId));
        }
    
        @GetMapping("/reviewer/{reviewerId}")
        public ResponseEntity<List<PerformanceReview>> getReviewsByReviewerId(@PathVariable String reviewerId) {
            return ResponseEntity.ok(performanceReviewService.getReviewsByReviewerId(reviewerId));
        }
    
        @GetMapping("/period/{reviewPeriod}")
        public ResponseEntity<List<PerformanceReview>> getReviewsByPeriod(@PathVariable String reviewPeriod) {
            return ResponseEntity.ok(performanceReviewService.getReviewsByPeriod(reviewPeriod));
        }
    
        @PutMapping("/{id}")
        public ResponseEntity<PerformanceReview> updateReview(
                @PathVariable Long id,
                @RequestBody PerformanceReviewDTO dto) {
        
            PerformanceReview review = performanceReviewService.updateReview(id, dto);
            return ResponseEntity.ok(review);
        }
    
        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long id) {
            performanceReviewService.deleteReview(id);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Performance review deleted successfully");
            return ResponseEntity.ok(response);
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - PERFORMANCE REVIEWS (ALIAS)  (source: management/performance_reviews/PerformanceReviewsAliasController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/performance/reviews")
    @Tag(name = "4. Management-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
    static class PerformanceReviewsAliasController {

        private final PerformanceReviewService performanceReviewService;

        public PerformanceReviewsAliasController(PerformanceReviewService performanceReviewService) {
            this.performanceReviewService = performanceReviewService;
        }

        @GetMapping
        public ResponseEntity<Map<String, Object>> getReviews() {
            List<PerformanceReview> all = performanceReviewService.getAllReviews();
            long pending = all.stream().filter(r -> r.getPerformanceRating() == null).count();
            long completed = all.stream().filter(r -> r.getPerformanceRating() != null).count();

            Map<String, Object> cycle = Map.of(
                "title", "Q2 2026 Performance Review",
                "deadline", "2026-07-15",
                "pending", pending,
                "status", pending > 0 ? "in_progress" : "completed",
                "statusLabel", pending > 0 ? "In Progress" : "Completed"
            );

            List<Map<String, Object>> members = new ArrayList<>();
            for (PerformanceReview r : all) {
                boolean isCompleted = r.getPerformanceRating() != null;
                String status = isCompleted ? "completed" : "pending";
                members.add(Map.of(
                    "id", r.getId(),
                    "name", r.getEmployeeName() != null ? r.getEmployeeName() : "Employee",
                    "role", "",
                    "lastReview", r.getReviewPeriod() != null ? r.getReviewPeriod() : "-",
                    "status", status,
                    "statusLabel", isCompleted ? "Completed" : "Pending"
                ));
            }

            return ResponseEntity.ok(Map.of("cycle", cycle, "members", members));
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - PROJECTS  (source: management/projects/ProjectController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/projects")
    @Tag(name = "4. Management-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MANAGEMENT')")
    static class ProjectController {
    
        private final ProjectService projectService;
    
        public ProjectController(ProjectService projectService) {
            this.projectService = projectService;
        }
    
        /**
         * GET /api/projects
         * Returns all projects
         */
        @GetMapping
        public ResponseEntity<List<Project>> getAllProjects() {
            return ResponseEntity.ok(projectService.getAllProjects());
        }
    
        /**
         * GET /api/projects/{id}
         * Returns project by ID
         */
        @GetMapping("/{id}")
        public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
            return ResponseEntity.ok(projectService.getProjectById(id));
        }
    
        /**
         * POST /api/projects
         * Creates a new project
         */
        @PostMapping
        public ResponseEntity<Project> createProject(@RequestBody ProjectDTO projectDTO) {
            Project project = projectService.createProject(projectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        }
    
        /**
         * GET /api/projects/status/{status}
         * Returns projects by status
         */
        @GetMapping("/status/{status}")
        public ResponseEntity<List<Project>> getProjectsByStatus(@PathVariable String status) {
            return ResponseEntity.ok(projectService.getProjectsByStatus(status));
        }
    
        /**
         * GET /api/projects/active
         * Returns all active projects
         */
        @GetMapping("/active")
        public ResponseEntity<List<Project>> getActiveProjects() {
            return ResponseEntity.ok(projectService.getActiveProjects());
        }
    
        /**
         * GET /api/projects/manager/{projectManager}
         * Returns projects managed by a user
         */
        @GetMapping("/manager/{projectManager}")
        public ResponseEntity<List<Project>> getProjectsByProjectManager(@PathVariable String projectManager) {
            return ResponseEntity.ok(projectService.getProjectsByProjectManager(projectManager));
        }
    
        /**
         * GET /api/projects/priority/{priority}
         * Returns projects by priority
         */
        @GetMapping("/priority/{priority}")
        public ResponseEntity<List<Project>> getProjectsByPriority(@PathVariable String priority) {
            return ResponseEntity.ok(projectService.getProjectsByPriority(priority));
        }
    
        /**
         * GET /api/projects/member/{teamMember}
         * Returns projects for a team member
         */
        @GetMapping("/member/{teamMember}")
        public ResponseEntity<List<Project>> getProjectsByTeamMember(@PathVariable String teamMember) {
            return ResponseEntity.ok(projectService.getProjectsByTeamMember(teamMember));
        }
    
        /**
         * PUT /api/projects/{id}
         * Updates a project
         */
        @PutMapping("/{id}")
        public ResponseEntity<Project> updateProject(
                @PathVariable Long id,
                @RequestBody ProjectDTO projectDTO) {
        
            Project project = projectService.updateProject(id, projectDTO);
            return ResponseEntity.ok(project);
        }
    
        /**
         * PUT /api/projects/{id}/status
         * Updates project status
         */
        @PutMapping("/{id}/status")
        public ResponseEntity<Project> updateProjectStatus(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String status = request.get("status");
            Project project = projectService.updateProjectStatus(id, status);
            return ResponseEntity.ok(project);
        }
    
        /**
         * PUT /api/projects/{id}/progress
         * Updates project progress (0-100)
         */
        @PutMapping("/{id}/progress")
        public ResponseEntity<Project> updateProjectProgress(
                @PathVariable Long id,
                @RequestBody Map<String, Integer> request) {
        
            Integer progress = request.get("progress");
            Project project = projectService.updateProjectProgress(id, progress);
            return ResponseEntity.ok(project);
        }
    
        /**
         * POST /api/projects/{id}/team
         * Adds a team member to the project
         */
        @PostMapping("/{id}/team")
        public ResponseEntity<Map<String, String>> addTeamMember(
                @PathVariable Long id,
                @RequestBody Map<String, String> request) {
        
            String teamMember = request.get("teamMember");
            projectService.addTeamMember(id, teamMember);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Team member added successfully");
            return ResponseEntity.ok(response);
        }
    
        /**
         * DELETE /api/projects/{id}/team/{teamMember}
         * Removes a team member from the project
         */
        @DeleteMapping("/{id}/team/{teamMember}")
        public ResponseEntity<Map<String, String>> removeTeamMember(
                @PathVariable Long id,
                @PathVariable String teamMember) {
        
            projectService.removeTeamMember(id, teamMember);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Team member removed successfully");
            return ResponseEntity.ok(response);
        }
    
        /**
         * DELETE /api/projects/{id}
         * Deletes a project
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<Map<String, String>> deleteProject(@PathVariable Long id) {
            projectService.deleteProject(id);
        
            Map<String, String> response = new HashMap<>();
            response.put("message", "Project deleted successfully");
            return ResponseEntity.ok(response);
        }
    }



    // --------------------------------------------------------------------------------
    // MANAGEMENT - TEAMS  (source: management/teams/ManagementTeamController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @Tag(name = "4. Management-Controller")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
    static class ManagementTeamController {

        @GetMapping("/api/teams")
        public ResponseEntity<List<Map<String, Object>>> getTeams() {
            List<Map<String, Object>> teams = new ArrayList<>();
            teams.add(Map.of("id", 1, "name", "Engineering", "color", "blue", "icon", "code", "memberCount", 12, "description", "Product development and infrastructure"));
            teams.add(Map.of("id", 2, "name", "Design", "color", "purple", "icon", "palette", "memberCount", 8, "description", "UI/UX and visual design"));
            teams.add(Map.of("id", 3, "name", "Marketing", "color", "green", "icon", "campaign", "memberCount", 6, "description", "Brand and growth marketing"));
            teams.add(Map.of("id", 4, "name", "Sales", "color", "amber", "icon", "trending_up", "memberCount", 10, "description", "Revenue and customer acquisition"));
            teams.add(Map.of("id", 5, "name", "HR", "color", "red", "icon", "people", "memberCount", 5, "description", "People operations and culture"));
            return ResponseEntity.ok(teams);
        }

        @GetMapping("/api/team/analytics")
        public ResponseEntity<Map<String, Object>> getTeamAnalytics() {
            List<Map<String, Object>> topPerformers = new ArrayList<>();
            topPerformers.add(Map.of("name", "Alice Johnson", "initials", "AJ", "bg", "bg-blue-100", "text", "text-blue-600", "title", "Lead Engineer", "score", 98));
            topPerformers.add(Map.of("name", "Bob Smith", "initials", "BS", "bg", "bg-purple-100", "text", "text-purple-600", "title", "Senior Designer", "score", 95));
            topPerformers.add(Map.of("name", "Carol Davis", "initials", "CD", "bg", "bg-emerald-100", "text", "text-emerald-600", "title", "Product Manager", "score", 92));

            List<Map<String, Object>> charts = new ArrayList<>();
            charts.add(Map.of("id", "team-productivity", "title", "Team Productivity"));
            charts.add(Map.of("id", "skill-growth", "title", "Skill Growth"));

            List<Map<String, Object>> kpis = new ArrayList<>();
            kpis.add(Map.of("label", "Team Score", "value", 92, "extraClass", "text-emerald-600"));
            kpis.add(Map.of("label", "Tasks Completed", "value", 156, "extraClass", "text-blue-600"));
            kpis.add(Map.of("label", "Avg Rating", "value", 4.8, "extraClass", "text-amber-600"));

            return ResponseEntity.ok(Map.of("topPerformers", topPerformers, "charts", charts, "kpis", kpis));
        }
    }


}


// ==============================================================================================
// PORTAL MODULE
// ==============================================================================================

class PortalControllers {


    // --------------------------------------------------------------------------------
    // PORTAL - NOTIFICATIONS  (source: portal/NotificationController.java)
    // --------------------------------------------------------------------------------

    @RestController
    @RequestMapping("/api/notifications")
    @Tag(name = "5. Notification-Controller")
    static class NotificationController {

        private final NotificationService service;

        public NotificationController(NotificationService service) {
            this.service = service;
        }

        @GetMapping("/unread")
        public ResponseEntity<?> unread(Authentication authentication) {
            String employeeCode = requireEmployeeCode(authentication);
            if (employeeCode == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }
            return ResponseEntity.ok(Map.of("count", service.unreadCount(employeeCode)));
        }

        @GetMapping
        public ResponseEntity<?> list(
                Authentication authentication,
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size) {
            String employeeCode = requireEmployeeCode(authentication);
            if (employeeCode == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }
            return ResponseEntity.ok(service.list(employeeCode, page, size));
        }

        @GetMapping("/latest")
        public ResponseEntity<?> latest(Authentication authentication) {
            String employeeCode = requireEmployeeCode(authentication);
            if (employeeCode == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }
            return ResponseEntity.ok(service.latest(employeeCode, 5));
        }

        @PutMapping("/{id}/read")
        public ResponseEntity<?> markRead(Authentication authentication, @PathVariable String id) {
            String employeeCode = requireEmployeeCode(authentication);
            if (employeeCode == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }
            return ResponseEntity.ok(service.markRead(employeeCode, id));
        }

        @PutMapping("/read-all")
        public ResponseEntity<?> markAllRead(Authentication authentication) {
            String employeeCode = requireEmployeeCode(authentication);
            if (employeeCode == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }
            service.markAllRead(employeeCode);
            return ResponseEntity.ok(Map.of("success", true));
        }

        private String requireEmployeeCode(Authentication authentication) {
            if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
                return null;
            }
            return authentication.getName();
        }
    }

}}

