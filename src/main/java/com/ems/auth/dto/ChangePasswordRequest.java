package com.ems.auth.dto;

public class ChangePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public String getCurrentPassword()  { return currentPassword; }
    public String getNewPassword()      { return newPassword; }
    public String getConfirmPassword()  { return confirmPassword; }

    public void setCurrentPassword(String p)  { this.currentPassword = p; }
    public void setNewPassword(String p)      { this.newPassword = p; }
    public void setConfirmPassword(String p)  { this.confirmPassword = p; }
}
