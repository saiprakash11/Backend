package com.ems.management.performance_reviews;

import com.ems.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;

    public PerformanceReviewService(PerformanceReviewRepository performanceReviewRepository) {
        this.performanceReviewRepository = performanceReviewRepository;
    }

    public PerformanceReview createReview(PerformanceReviewDTO dto) {
        PerformanceReview review = new PerformanceReview();
        review.setEmployeeId(dto.getEmployeeId());
        review.setEmployeeName(dto.getEmployeeName());
        review.setReviewerId(dto.getReviewerId());
        review.setReviewerName(dto.getReviewerName());
        review.setPerformanceRating(dto.getPerformanceRating());
        review.setStrengths(dto.getStrengths());
        review.setAreasForImprovement(dto.getAreasForImprovement());
        review.setOverallComments(dto.getOverallComments());
        review.setReviewPeriod(dto.getReviewPeriod());
        // reviewDate, createdAt, updatedAt set by @PrePersist
        return performanceReviewRepository.save(review);
    }

    public PerformanceReview getReviewById(Long id) {
        return performanceReviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    public List<PerformanceReview> getAllReviews() {
        return performanceReviewRepository.findAll();
    }

    public List<PerformanceReview> getReviewsByEmployeeId(String employeeId) {
        return performanceReviewRepository.findByEmployeeId(employeeId);
    }

    public List<PerformanceReview> getReviewsByReviewerId(String reviewerId) {
        return performanceReviewRepository.findByReviewerId(reviewerId);
    }

    public List<PerformanceReview> getReviewsByPeriod(String reviewPeriod) {
        return performanceReviewRepository.findByReviewPeriod(reviewPeriod);
    }

    public PerformanceReview updateReview(Long id, PerformanceReviewDTO dto) {
        PerformanceReview review = getReviewById(id);

        if (dto.getPerformanceRating() != null) {
            review.setPerformanceRating(dto.getPerformanceRating());
        }
        if (dto.getStrengths() != null) {
            review.setStrengths(dto.getStrengths());
        }
        if (dto.getAreasForImprovement() != null) {
            review.setAreasForImprovement(dto.getAreasForImprovement());
        }
        if (dto.getOverallComments() != null) {
            review.setOverallComments(dto.getOverallComments());
        }

        // updatedAt handled by @PreUpdate
        return performanceReviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        PerformanceReview review = getReviewById(id);
        performanceReviewRepository.delete(review);
    }
}
