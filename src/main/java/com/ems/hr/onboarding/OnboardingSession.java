package com.ems.hr.onboarding;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "onboarding_sessions",
        indexes = {
                @Index(name = "idx_onboarding_sessions_session_id", columnList = "session_id", unique = true),
                @Index(name = "idx_onboarding_sessions_employee_code", columnList = "employee_code")
        }
)
public class OnboardingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 64)
    private String sessionId;

    @Column(name = "employee_code", length = 20)
    private String employeeCode;

    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 1;

    @Column(name = "session_data_json", nullable = false, columnDefinition = "LONGTEXT")
    private String sessionDataJson = "{}";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "last_step_at")
    private LocalDateTime lastStepAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (lastStepAt == null) {
            lastStepAt = createdAt;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
    }

    public String getSessionDataJson() {
        return sessionDataJson;
    }

    public void setSessionDataJson(String sessionDataJson) {
        this.sessionDataJson = sessionDataJson;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastStepAt() {
        return lastStepAt;
    }

    public void setLastStepAt(LocalDateTime lastStepAt) {
        this.lastStepAt = lastStepAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
