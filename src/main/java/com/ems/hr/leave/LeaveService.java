package com.ems.hr.leave;

import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeStore;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Service
public class LeaveService {
    private final LeaveStore store;
    private final EmployeeStore employeeStore;
    LeaveService(LeaveStore store, EmployeeStore employeeStore) {
        this.store         = store;
        this.employeeStore = employeeStore;
    }
    /** Applies a new leave — called when employee submits the leave form. */
    String applyLeave(Map<String, String> body) {
        String employeeId = body.get("employeeId");
        String fromDate = body.get("fromDate");
        String toDate = body.get("toDate");
        String reason = body.get("reason");
        Employee emp      = employeeStore.findById(employeeId);
        String employeeName = emp != null ? emp.getName() : employeeId;

        if (body.get("leaveType") == null || body.get("leaveType").isBlank()) {
            return "Leave type is required.";
        }
        if (fromDate == null || toDate == null || reason == null || reason.isBlank()) {
            return "Leave type, dates, and reason are required.";
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            startDate = LocalDate.parse(fromDate);
            endDate = LocalDate.parse(toDate);
        } catch (Exception ex) {
            return "Please provide a valid date range.";
        }

        if (endDate.isBefore(startDate)) {
            return "End date cannot be before start date.";
        }
        LeaveRequest req = new LeaveRequest(
                store.nextId(),
                employeeId,
                employeeName,
                body.get("leaveType"),
                fromDate,
                toDate,
                reason,
                "Pending"
        );
        store.add(req);
        return "Leave applied successfully. ID: " + req.getId();
    }
    /** Admin approves a leave — triggered by the Approve button. */
    String approveLeave(String id) {
        LeaveRequest req = store.findById(id);
        if (req == null) return "Leave request not found: " + id;
        req.setStatus("Approved");
        store.save(req);
        return "Leave approved";
    }

    /** Admin rejects a leave — triggered by the Reject button. */
    String rejectLeave(String id) {
        LeaveRequest req = store.findById(id);
        if (req == null) return "Leave request not found: " + id;
        req.setStatus("Rejected");
        store.save(req);
        return "Leave rejected";
    }

    List<LeaveRequest> getAll() { return store.getAll(); }
}

@RestController
@RequestMapping("/api/leave")
class LeaveController {

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
