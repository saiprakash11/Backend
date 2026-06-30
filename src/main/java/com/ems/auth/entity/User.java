package com.ems.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", length = 20)
    private String employeeCode;

    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "role")
    private String role;

    @Column(name = "is_alive")
    private Boolean isAlive;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "profile_photo", length = 500)
    private String profilePhoto;

    @Column(name = "profile_photo_name")
    private String profilePhotoName;

    @Column(name = "profile_photo_content_type")
    private String profilePhotoContentType;

    @Column(name = "first_login")
    private LocalDateTime firstLogin;

    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "modified_on", nullable = false)
    private LocalDateTime modifiedOn;

    @Column(name = "modified_by")
    private String modifiedBy;

    @PrePersist
    protected void onCreate() {
        createdOn  = LocalDateTime.now();
        modifiedOn = LocalDateTime.now();
        if (createdBy == null) createdBy = "SYSTEM";
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedOn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return username; }
    public void setEmail(String email) { this.username = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getIsAlive() { return isAlive; }
    public void setIsAlive(Boolean isAlive) { this.isAlive = isAlive; }
    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }
    public String getProfilePhoto() { return profilePhoto; }
    public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
    public String getProfilePhotoName() { return profilePhotoName; }
    public void setProfilePhotoName(String profilePhotoName) { this.profilePhotoName = profilePhotoName; }
    public String getProfilePhotoContentType() { return profilePhotoContentType; }
    public void setProfilePhotoContentType(String profilePhotoContentType) { this.profilePhotoContentType = profilePhotoContentType; }
    public LocalDateTime getFirstLogin() { return firstLogin; }
    public void setFirstLogin(LocalDateTime firstLogin) { this.firstLogin = firstLogin; }
    public LocalDateTime getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDateTime createdOn) { this.createdOn = createdOn; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getModifiedOn() { return modifiedOn; }
    public void setModifiedOn(LocalDateTime modifiedOn) { this.modifiedOn = modifiedOn; }
    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
}
