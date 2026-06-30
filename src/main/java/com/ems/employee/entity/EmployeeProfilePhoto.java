package com.ems.employee.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_profile_photos")
public class EmployeeProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", length = 20, nullable = false)
    private String employeeCode;

    @Column(name = "photo_name", length = 255)
    private String photoName;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Lob
    @Column(name = "photo_data", columnDefinition = "LONGBLOB NOT NULL")
    private byte[] photoData;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) uploadedAt = LocalDateTime.now();
        if (isActive == null) isActive = false;
    }

    public EmployeeProfilePhoto() {}

    public EmployeeProfilePhoto(String employeeCode, String photoName, String contentType, byte[] photoData) {
        this.employeeCode = employeeCode;
        this.photoName = photoName;
        this.contentType = contentType;
        this.photoData = photoData;
        this.isActive = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getPhotoName() { return photoName; }
    public void setPhotoName(String photoName) { this.photoName = photoName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public byte[] getPhotoData() { return photoData; }
    public void setPhotoData(byte[] photoData) { this.photoData = photoData; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
