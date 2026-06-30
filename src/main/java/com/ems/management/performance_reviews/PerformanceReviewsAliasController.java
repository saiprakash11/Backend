package com.ems.management.performance_reviews;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance/reviews")
@PreAuthorize("hasAnyRole('ADMIN','HR','MANAGEMENT')")
public class PerformanceReviewsAliasController {

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
