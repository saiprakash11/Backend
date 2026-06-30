package com.ems.management.approvals;

import java.time.LocalDateTime;

public class ApprovalDTO {

    private Long id;
    private String requestId;
    private String requestType;
    private String requestedBy;
    private String approver;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private String comments;
    private Double amount;
    private LocalDateTime updatedAt;

    public ApprovalDTO() {}

    public ApprovalDTO(Long id, String requestId, String requestType, String requestedBy,
                       String approver, String status, String description, LocalDateTime createdAt,
                       LocalDateTime approvedAt, String comments, Double amount, LocalDateTime updatedAt) {
        this.id = id;
        this.requestId = requestId;
        this.requestType = requestType;
        this.requestedBy = requestedBy;
        this.approver = approver;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.comments = comments;
        this.amount = amount;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getApprover() { return approver; }
    public void setApprover(String approver) { this.approver = approver; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
