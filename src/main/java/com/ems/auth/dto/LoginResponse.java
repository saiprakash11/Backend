package com.ems.auth.dto;

public class LoginResponse {

    private String  message;
    private String  role;
    private boolean success;
    private String  email;
    private String  fullName;
    private String  employeeCode;
    private String  token;
    private String  redirectUrl;

    public LoginResponse(String message, String role, boolean success,
                         String email, String fullName, String employeeCode,
                         String token, String redirectUrl) {
        this.message      = message;
        this.role         = role;
        this.success      = success;
        this.email        = email;
        this.fullName     = fullName;
        this.employeeCode = employeeCode;
        this.token        = token;
        this.redirectUrl  = redirectUrl;
    }

    // ── Getters ──────────────────────────────────────────

    public String  getMessage()      { return message; }
    public String  getRole()         { return role; }
    public boolean isSuccess()       { return success; }
    public String  getEmail()        { return email; }
    public String  getFullName()     { return fullName; }
    public String  getEmployeeCode() { return employeeCode; }
    public String  getToken()        { return token; }
    public String  getRedirectUrl()  { return redirectUrl; }

    // ── Setters ──────────────────────────────────────────

    public void setMessage(String message)           { this.message = message; }
    public void setRole(String role)                 { this.role = role; }
    public void setSuccess(boolean success)          { this.success = success; }
    public void setEmail(String email)               { this.email = email; }
    public void setFullName(String fullName)         { this.fullName = fullName; }
    public void setEmployeeCode(String code)         { this.employeeCode = code; }
    public void setToken(String token)               { this.token = token; }
    public void setRedirectUrl(String redirectUrl)   { this.redirectUrl = redirectUrl; }
}
