package com.ems.hr.attendance;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
public class AttendanceController {

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
