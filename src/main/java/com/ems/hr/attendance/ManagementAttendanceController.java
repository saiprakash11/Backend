package com.ems.hr.attendance;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/management/attendance")
@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
public class ManagementAttendanceController {

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