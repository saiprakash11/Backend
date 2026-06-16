/*
 * package com.authentication.controller;
 * 
 * import com.authentication.dto.ChangePasswordRequest; import
 * com.authentication.dto.LoginRequest; import
 * com.authentication.dto.LoginResponse; import
 * com.authentication.service.AuthService; import com.security.JwtUtil; import
 * org.springframework.http.HttpStatus; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.jdbc.core.JdbcTemplate; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.web.bind.annotation.*;
 * 
 * import java.util.List; import java.util.Map;
 * 
 * @RestController
 * 
 * @RequestMapping("/api/auth") public class AuthController {
 * 
 * private final AuthService authService; private final JwtUtil jwtUtil; private
 * final JdbcTemplate jdbcTemplate; private final PasswordEncoder
 * passwordEncoder;
 * 
 * public AuthController(AuthService authService, JwtUtil jwtUtil, JdbcTemplate
 * jdbcTemplate, PasswordEncoder passwordEncoder) { this.authService =
 * authService; this.jwtUtil = jwtUtil; this.jdbcTemplate = jdbcTemplate;
 * this.passwordEncoder = passwordEncoder; }
 * 
 * @PostMapping("/login") public ResponseEntity<LoginResponse>
 * login(@RequestBody LoginRequest request) { LoginResponse response =
 * authService.login(request); if (!response.isSuccess()) { return
 * ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); } return
 * ResponseEntity.ok(response); }
 * 
 * @PostMapping("/logout") public ResponseEntity<Map<String, String>> logout(
 * 
 * @RequestHeader(value = "Authorization", required = false) String authHeader)
 * { return ResponseEntity.ok(Map.of("message", "Logged out successfully.")); }
 * 
 * @PostMapping("/change-password") public ResponseEntity<LoginResponse>
 * changePassword(
 * 
 * @RequestHeader("Authorization") String authHeader,
 * 
 * @RequestBody ChangePasswordRequest request) {
 * 
 * String token = authHeader.replace("Bearer ", "").trim();
 * 
 * if (!jwtUtil.isValid(token)) { return
 * ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body(new
 * LoginResponse("Invalid or expired token.", null, false, null, null, null,
 * null, null)); }
 * 
 * if (request.getNewPassword() != null &&
 * !request.getNewPassword().equals(request.getConfirmPassword())) { return
 * ResponseEntity.badRequest() .body(new
 * LoginResponse("Passwords do not match.", null, false, null, null, null, null,
 * null)); }
 * 
 * String employeeCode = jwtUtil.getEmployeeCode(token); LoginResponse response
 * = authService.changePassword( employeeCode, request.getCurrentPassword(),
 * request.getNewPassword());
 * 
 * if (!response.isSuccess()) { return
 * ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); }
 * 
 * return ResponseEntity.ok(response); }
 * 
 * @GetMapping("/me") public ResponseEntity<Map<String, String>> me(
 * 
 * @RequestHeader("Authorization") String authHeader) {
 * 
 * String token = authHeader.replace("Bearer ", "").trim();
 * 
 * if (!jwtUtil.isValid(token)) { return
 * ResponseEntity.status(HttpStatus.UNAUTHORIZED) .body(Map.of("error",
 * "Invalid or expired token.")); }
 * 
 * return ResponseEntity.ok(Map.of( "employeeCode",
 * jwtUtil.getEmployeeCode(token), "role", jwtUtil.getRole(token))); }
 * 
 * @PostMapping("/clear-users") public ResponseEntity<Map<String, String>>
 * clearUsers() { try { jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
 * jdbcTemplate.execute("DELETE FROM user_roles");
 * jdbcTemplate.execute("DELETE FROM employees");
 * jdbcTemplate.execute("DELETE FROM employee_profiles");
 * jdbcTemplate.execute("DELETE FROM users");
 * jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1"); return
 * ResponseEntity.ok(Map.of("status", "success", "message", "Users cleared")); }
 * catch (Exception e) { return
 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 * .body(Map.of("status", "error", "message", e.getMessage())); } }
 * 
 * @PostMapping("/insert-test-users") public ResponseEntity<Map<String, String>>
 * insertTestUsers() { try { // First check if users already exist Integer count
 * = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
 * if (count != null && count > 0) { return ResponseEntity.ok(Map.of( "status",
 * "success", "message", "Users already exist" )); }
 * 
 * // Insert roles first List<Object[]> roles = List.of( new Object[]{1,
 * "ADMIN", "Administrator", "Full system access"}, new Object[]{2, "HR",
 * "Human Resources", "HR operations"}, new Object[]{3, "MANAGER", "Manager",
 * "Team management"}, new Object[]{4, "EMPLOYEE", "Employee",
 * "Self-service portal"} );
 * 
 * for (Object[] role : roles) { jdbcTemplate.
 * update("INSERT IGNORE INTO roles (id, role_code, role_name, description) VALUES (?, ?, ?, ?)"
 * , role); }
 * 
 * // Predefined users String pwdHash = passwordEncoder.encode("root");
 * List<Object[]> users = List.of( new Object[]{"EMP-ADMIN-001",
 * "admin@company.com", pwdHash, "ADMIN", "Karthik Raman", "Administration",
 * "System Admin", 100000}, new Object[]{"EMP-HR-001", "hr@company.com",
 * pwdHash, "HR", "Priya Nair", "Human Resources", "HR Manager", 75000}, new
 * Object[]{"EMP-MGR-001", "manager@company.com", pwdHash, "MANAGER",
 * "Rahul Sharma", "Engineering", "Engineering Manager", 125000}, new
 * Object[]{"EMP-001", "arun@company.com", pwdHash, "EMPLOYEE", "Arun Kumar",
 * "Engineering", "Software Engineer", 55000} );
 * 
 * for (Object[] user : users) { String empCode = (String) user[0]; String
 * username = (String) user[1]; String role = (String) user[3]; String fullName
 * = (String) user[4]; String dept = (String) user[5]; String desig = (String)
 * user[6]; int salary = (int) user[7];
 * 
 * // Insert into users jdbcTemplate.
 * update("INSERT INTO users (employee_code, username, password_hash, role, is_active, is_alive) VALUES (?, ?, ?, ?, 1, 1)"
 * , empCode, username, user[2], role);
 * 
 * // Get user ID Long userId =
 * jdbcTemplate.queryForObject("SELECT id FROM users WHERE employee_code = ?",
 * Long.class, empCode);
 * 
 * // Insert user roles int roleId = "ADMIN".equals(role) ? 1 :
 * ("HR".equals(role) ? 2 : ("MANAGER".equals(role) ? 3 : 4));
 * jdbcTemplate.update("INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)"
 * , userId, roleId);
 * 
 * // Insert into employees jdbcTemplate.
 * update("INSERT INTO employees (id, name, email, phone, department, designation, join_date, status, salary) VALUES (?, ?, ?, '9876543210', ?, ?, '2024-01-01', 'Active', ?)"
 * , empCode, fullName, username, dept, desig, salary);
 * 
 * // Insert into employee_profiles jdbcTemplate.
 * update("INSERT INTO employee_profiles (employee_code, full_name, email, phone_number, department, designation, date_of_joining, work_location, salary, status, gender) VALUES (?, ?, ?, '9876543210', ?, ?, '2025-01-10', 'Bengaluru', ?, 'Active', 'Male')"
 * , empCode, fullName, username, dept, desig, salary); }
 * 
 * return ResponseEntity.ok(Map.of( "status", "success", "message",
 * "Successfully inserted test users", "users",
 * "admin@company.com, hr@company.com, manager@company.com, arun@company.com (password: root)"
 * )); } catch (Exception e) { return
 * ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
 * .body(Map.of("status", "error", "message", e.getMessage())); } } }
 */