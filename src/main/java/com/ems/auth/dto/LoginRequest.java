package com.ems.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login credentials")
public class LoginRequest {

    @Schema(description = "User email address", example = "admin@company.com")
    private String email;

    @Schema(description = "Plain-text password (NOT a hash)", example = "Admin@123")
    private String password;

    private boolean rememberMe;

    public String getEmail()                    { return email; }
    public void   setEmail(String email)        { this.email = email; }
    public String getPassword()                 { return password; }
    public void   setPassword(String password)  { this.password = password; }
    public boolean isRememberMe()               { return rememberMe; }
    public void    setRememberMe(boolean v)     { this.rememberMe = v; }
}
