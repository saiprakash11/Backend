package com.ems.hr.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardMetricsController {

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
