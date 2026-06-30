package com.ems.management.performance_reviews;

import java.time.LocalDateTime;

public class PerformanceReviewDTO {

    private Long id;
    private String employeeId;
    private String employeeName;
    private String reviewerId;
    private String reviewerName;
    private Integer performanceRating;
    private String strengths;
    private String areasForImprovement;
    private String overallComments;
    private String reviewPeriod;
    private LocalDateTime reviewDate;
    private LocalDateTime nextReviewDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PerformanceReviewDTO() {}

    public PerformanceReviewDTO(Long id, String employeeId, String employeeName, String reviewerId,
                                String reviewerName, Integer performanceRating, String strengths,
                                String areasForImprovement, String overallComments, String reviewPeriod,
                                LocalDateTime reviewDate, LocalDateTime nextReviewDate,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.employeeId = employeeId; this.employeeName = employeeName;
        this.reviewerId = reviewerId; this.reviewerName = reviewerName;
        this.performanceRating = performanceRating; this.strengths = strengths;
        this.areasForImprovement = areasForImprovement; this.overallComments = overallComments;
        this.reviewPeriod = reviewPeriod; this.reviewDate = reviewDate;
        this.nextReviewDate = nextReviewDate; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public Integer getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }

    public String getOverallComments() { return overallComments; }
    public void setOverallComments(String overallComments) { this.overallComments = overallComments; }

    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public LocalDateTime getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDateTime nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
