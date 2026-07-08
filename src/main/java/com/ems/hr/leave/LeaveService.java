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
    public String applyLeave(Map<String, String> body) {
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
    public String approveLeave(String id) {
        LeaveRequest req = store.findById(id);
        if (req == null) return "Leave request not found: " + id;
        req.setStatus("Approved");
        store.save(req);
        return "Leave approved";
    }

    /** Admin rejects a leave — triggered by the Reject button. */
    public String rejectLeave(String id) {
        LeaveRequest req = store.findById(id);
        if (req == null) return "Leave request not found: " + id;
        req.setStatus("Rejected");
        store.save(req);
        return "Leave rejected";
    }

    public List<LeaveRequest> getAll() { return store.getAll(); }
}
// NOTE: LeaveController was moved to com.ems.controllers.AllControllers.java

