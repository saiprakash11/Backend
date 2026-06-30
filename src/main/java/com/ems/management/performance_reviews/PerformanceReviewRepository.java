package com.ems.management.performance_reviews;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    
    List<PerformanceReview> findByEmployeeId(String employeeId);
    
    List<PerformanceReview> findByReviewerId(String reviewerId);
    
    List<PerformanceReview> findByReviewPeriod(String reviewPeriod);
}
