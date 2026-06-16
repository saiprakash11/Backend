/*
 * package com.authentication.service;
 * 
 * import com.authentication.dto.LoginRequest; import
 * com.authentication.dto.LoginResponse; import com.authentication.entity.User;
 * import com.authentication.repository.UserRepository; import
 * org.springframework.dao.EmptyResultDataAccessException; import
 * org.springframework.jdbc.core.JdbcTemplate; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.stereotype.Service;
 * 
 * import java.time.LocalDateTime; import java.util.Optional;
 * 
 * @Service public class AuthService {
 * 
 * private final UserRepository userRepository; private final PasswordEncoder
 * passwordEncoder; private final JdbcTemplate jdbcTemplate;
 * 
 * public AuthService(UserRepository userRepository, PasswordEncoder
 * passwordEncoder, JdbcTemplate jdbcTemplate) { this.userRepository =
 * userRepository; this.passwordEncoder = passwordEncoder; this.jdbcTemplate =
 * jdbcTemplate; }
 * 
 * public LoginResponse login(LoginRequest request) {
 * 
 * if (request.getEmail() == null || request.getPassword() == null) { return
 * fail("Email and password are required."); }
 * 
 * String loginId = request.getEmail().trim().toLowerCase(); Optional<User>
 * userOpt = userRepository.findByUsername(loginId); if (userOpt.isEmpty()) {
 * userOpt = userRepository.findByUsernameIgnoreCase(loginId); }
 * 
 * if (userOpt.isEmpty()) { return fail("Invalid email or password."); }
 * 
 * User user = userOpt.get();
 * 
 * if (Boolean.FALSE.equals(user.getIsAlive())) { return
 * fail("Your account has been deactivated. Please contact HR."); }
 * 
 * if (!matchesPassword(request.getPassword(), user.getPasswordHash())) { return
 * fail("Invalid email or password."); }
 * 
 * user.setLastActive(LocalDateTime.now()); userRepository.save(user); return
 * new LoginResponse( "Login successful", user.getRole(), true,
 * user.getUsername(), fullNameFor(user.getEmployeeCode(), user.getUsername()),
 * user.getEmployeeCode(), token, redirectUrl(user.getRole()) ); }
 * 
 * public LoginResponse changePassword(String employeeCode, String
 * currentPassword, String newPassword) {
 * 
 * Optional<User> userOpt = userRepository.findByEmployeeCode(employeeCode);
 * 
 * if (userOpt.isEmpty()) { return fail("User not found."); }
 * 
 * User user = userOpt.get();
 * 
 * if (!matchesPassword(currentPassword, user.getPasswordHash())) { return
 * fail("Current password is incorrect."); }
 * 
 * if (newPassword == null || newPassword.length() < 8) { return
 * fail("New password must be at least 8 characters."); }
 * 
 * user.setPasswordHash(passwordEncoder.encode(newPassword));
 * userRepository.save(user);
 * 
 * return new LoginResponse("Password changed successfully.", user.getRole(),
 * true, user.getUsername(), fullNameFor(user.getEmployeeCode(),
 * user.getUsername()), user.getEmployeeCode(), null, null); }
 * 
 * // ── Helpers ──────────────────────────────────────────
 * 
 * private boolean matchesPassword(String raw, String stored) { if (stored ==
 * null || raw == null) { return false; }
 * 
 * if (!(stored.startsWith("$2a$") || stored.startsWith("$2b$") ||
 * stored.startsWith("$2y$"))) { return false; }
 * 
 * try { return passwordEncoder.matches(raw, stored); } catch
 * (IllegalArgumentException ex) { return false; } }
 * 
 * private String redirectUrl(String role) { if (role == null) return
 * "employee/employee_dashboard/code.html"; return switch (role.toUpperCase()) {
 * case "ADMIN" -> "admin_hr/admin_hr_dashboard/code.html"; case "HR" ->
 * "admin_hr/admin_hr_dashboard/code.html"; case "MANAGER" ->
 * "management/management_dashboard/code.html"; case "MANAGEMENT" ->
 * "management/management_dashboard/code.html"; default ->
 * "employee/employee_dashboard/code.html"; }; }
 * 
 * private String fullNameFor(String employeeCode, String fallback) { try {
 * String fullName = jdbcTemplate.queryForObject(
 * "select coalesce(ep.full_name, e.name, u.username) " + "from users u " +
 * "left join employee_profiles ep on ep.employee_code = u.employee_code " +
 * "left join employees e on e.id = u.employee_code " +
 * "where u.employee_code = ?", String.class, employeeCode ); return fullName ==
 * null || fullName.isBlank() ? fallback : fullName; } catch
 * (EmptyResultDataAccessException ex) { return fallback; } }
 * 
 * private LoginResponse fail(String message) { return new
 * LoginResponse(message, null, false, null, null, null, null, null); } }
 */