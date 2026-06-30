package com.ems.hr.assets;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_assets")
public class EmployeeAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, length = 50)
    private String employeeCode;

    @Column(name = "company_asset_id", nullable = false)
    private Long companyAssetId;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) assignedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public Long getCompanyAssetId() { return companyAssetId; }
    public void setCompanyAssetId(Long companyAssetId) { this.companyAssetId = companyAssetId; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
