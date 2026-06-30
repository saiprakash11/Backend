package com.ems.hr.recruitment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Entity
@Table(name = "recruitment")
public class RecruitmentCandidate {

    @Id
    private String id;

    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    private String phone;
    @NotBlank
    private String position;
    private String department;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    private String status;

    public RecruitmentCandidate() {}

    @PrePersist
    protected void onCreate() {
        if (status == null || status.isBlank()) {
            status = "Applied";
        }
        if (appliedDate == null) {
            appliedDate = LocalDate.now();
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
