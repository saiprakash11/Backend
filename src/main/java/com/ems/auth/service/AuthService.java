package com.ems.auth.service;

import com.ems.auth.dto.LoginRequest;
import com.ems.auth.dto.LoginResponse;
import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import com.ems.employee.entity.EmployeeProfile;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeRepository;
import com.ems.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository           userRepository;
    private final PasswordEncoder          passwordEncoder;
    private final JwtUtil                  jwtUtil;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final EmployeeRepository       employeeRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmployeeProfileRepository employeeProfileRepository,
                       EmployeeRepository employeeRepository) {
        this.userRepository           = userRepository;
        this.passwordEncoder          = passwordEncoder;
        this.jwtUtil                  = jwtUtil;
        this.employeeProfileRepository = employeeProfileRepository;
        this.employeeRepository       = employeeRepository;
    }

    public LoginResponse login(LoginRequest request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            return fail("Email and password are required.");
        }

        String loginId = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByUsername(loginId);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsernameIgnoreCase(loginId);
        }

        if (userOpt.isEmpty()) {
            return fail("Invalid email or password.");
        }

        User user = userOpt.get();

        if (Boolean.FALSE.equals(user.getIsAlive())) {
            return fail("Your account has been deactivated. Please contact HR.");
        }

        if (!matchesPassword(request.getPassword(), user.getPasswordHash())) {
            return fail("Invalid email or password.");
        }

        user.setLastActive(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmployeeCode(), user.getRole());

        return new LoginResponse(
                "Login successful",
                user.getRole(),
                true,
                user.getUsername(),
                fullNameFor(user.getEmployeeCode(), user.getUsername()),
                user.getEmployeeCode(),
                token,
                redirectUrl(user.getRole())
        );
    }

    public LoginResponse changePassword(String employeeCode,
                                        String currentPassword,
                                        String newPassword) {

        Optional<User> userOpt = userRepository.findByEmployeeCode(employeeCode);

        if (userOpt.isEmpty()) {
            return fail("User not found.");
        }

        User user = userOpt.get();

        if (!matchesPassword(currentPassword, user.getPasswordHash())) {
            return fail("Current password is incorrect.");
        }

        if (newPassword == null || newPassword.length() < 8) {
            return fail("New password must be at least 8 characters.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new LoginResponse("Password changed successfully.",
                user.getRole(), true, user.getUsername(),
                fullNameFor(user.getEmployeeCode(), user.getUsername()),
                user.getEmployeeCode(), null, null);
    }

    private boolean matchesPassword(String raw, String stored) {
        if (stored == null || raw == null) {
            return false;
        }

        if (!(stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"))) {
            return false;
        }

        try {
            return passwordEncoder.matches(raw, stored);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private String redirectUrl(String role) {
        if (role == null) return "employee/employee_dashboard/code.html";
        return switch (role.toUpperCase()) {
            case "ADMIN"      -> "admin_hr/admin_hr_dashboard/code.html";
            case "HR"         -> "admin_hr/admin_hr_dashboard/code.html";
            case "MANAGER"    -> "management/management_dashboard/code.html";
            case "MANAGEMENT" -> "management/management_dashboard/code.html";
            default           -> "employee/employee_dashboard/code.html";
        };
    }

    private String fullNameFor(String employeeCode, String fallback) {
        EmployeeProfile profile = employeeProfileRepository.findByEmployeeCode(employeeCode);
        if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
            return profile.getFullName();
        }
        Optional<Employee> empOpt = employeeRepository.findById(employeeCode);
        if (empOpt.isPresent() && empOpt.get().getName() != null && !empOpt.get().getName().isBlank()) {
            return empOpt.get().getName();
        }
        return fallback;
    }

    private LoginResponse fail(String message) {
        return new LoginResponse(message, null, false, null, null, null, null, null);
    }
}
