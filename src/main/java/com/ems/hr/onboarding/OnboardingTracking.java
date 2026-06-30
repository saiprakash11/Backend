package com.ems.hr.onboarding;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_tracking")
public class OnboardingTracking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, length = 20)
    private String employeeCode;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "current_step")
    private String currentStep = "Personal Details";

    @Column(name = "current_step_num")
    private Integer currentStepNum = 1;

    @Column(name = "total_steps")
    private Integer totalSteps = 5;

    @Column(name = "progress_percent")
    private Integer progressPercent = 0;

    @Column(name = "assigned_hr")
    private String assignedHr;

    @Column(name = "status")
    private String status = "Pending"; // Pending | In Progress | Completed

    @Column(name = "step1_done") private Boolean step1Done = false;
    @Column(name = "step2_done") private Boolean step2Done = false;
    @Column(name = "step3_done") private Boolean step3Done = false;
    @Column(name = "step4_done") private Boolean step4Done = false;
    @Column(name = "step5_done") private Boolean step5Done = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        recalcProgress();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        recalcProgress();
    }

    public void recalcProgress() {
        int done = 0;
        if (Boolean.TRUE.equals(step1Done)) done++;
        if (Boolean.TRUE.equals(step2Done)) done++;
        if (Boolean.TRUE.equals(step3Done)) done++;
        if (Boolean.TRUE.equals(step4Done)) done++;
        if (Boolean.TRUE.equals(step5Done)) done++;
        this.progressPercent = (done * 100) / (totalSteps != null ? totalSteps : 5);

        String[] stepNames = {"Personal Details","Job Role & Dept","Payroll & Benefits","Documents Upload","IT Setup & Access"};
        if (progressPercent == 100) {
            this.status = "Completed";
            this.currentStep = "Completed";
            this.currentStepNum = 5;
        } else if (done > 0) {
            this.status = "In Progress";
            this.currentStepNum = done + 1;
            this.currentStep = stepNames[Math.min(done, 4)];
        } else {
            this.status = "Pending";
            this.currentStepNum = 1;
            this.currentStep = stepNames[0];
        }
    }

    // ── Getters & Setters ─────────────────────────────────
    public Long getId() { return id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String v) { this.employeeCode = v; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String v) { this.employeeName = v; }
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }
    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate v) { this.joiningDate = v; }
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String v) { this.currentStep = v; }
    public Integer getCurrentStepNum() { return currentStepNum; }
    public void setCurrentStepNum(Integer v) { this.currentStepNum = v; }
    public Integer getTotalSteps() { return totalSteps; }
    public void setTotalSteps(Integer v) { this.totalSteps = v; }
    public Integer getProgressPercent() { return progressPercent; }
    public void setProgressPercent(Integer v) { this.progressPercent = v; }
    public String getAssignedHr() { return assignedHr; }
    public void setAssignedHr(String v) { this.assignedHr = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public Boolean getStep1Done() { return step1Done; }
    public void setStep1Done(Boolean v) { this.step1Done = v; }
    public Boolean getStep2Done() { return step2Done; }
    public void setStep2Done(Boolean v) { this.step2Done = v; }
    public Boolean getStep3Done() { return step3Done; }
    public void setStep3Done(Boolean v) { this.step3Done = v; }
    public Boolean getStep4Done() { return step4Done; }
    public void setStep4Done(Boolean v) { this.step4Done = v; }
    public Boolean getStep5Done() { return step5Done; }
    public void setStep5Done(Boolean v) { this.step5Done = v; }
    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
