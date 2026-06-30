package com.ems.management.performance_reviews;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance-reviews")
public class PerformanceReviewController {
    
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
