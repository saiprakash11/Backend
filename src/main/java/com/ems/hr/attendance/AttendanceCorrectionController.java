package com.ems.hr.attendance;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/attendance/corrections")
@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
public class AttendanceCorrectionController {

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