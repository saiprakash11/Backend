package com.ems.hr.userstatus;

import com.ems.auth.entity.User;
import com.ems.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserStatusController – manages employee active/inactive status.
 *
 * GET  /api/users                          → list all users
 * PUT  /api/users/{employeeCode}/activate  → set is_alive = true
 * PUT  /api/users/{employeeCode}/deactivate→ set is_alive = false
 * GET  /api/users/{employeeCode}/status    → get current status
 */
@RestController
@RequestMapping("/api/users")
public class UserStatusController {

    private final UserRepository userRepository;

    public UserStatusController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(u -> Map.<String, Object>of(
                "employeeCode", u.getEmployeeCode() != null ? u.getEmployeeCode() : "",
                "email",        u.getUsername(),
                "role",         u.getRole(),
                "status",       Boolean.TRUE.equals(u.getIsAlive()) ? "Active" : "Inactive"
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{employeeCode}/activate")
    public ResponseEntity<Map<String, Object>> activate(@PathVariable String employeeCode) {
        return toggleStatus(employeeCode, true);
    }

    @PutMapping("/{employeeCode}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable String employeeCode) {
        return toggleStatus(employeeCode, false);
    }

    @GetMapping("/{employeeCode}/status")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable String employeeCode) {
        Optional<User> opt = userRepository.findByEmployeeCode(employeeCode);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User u = opt.get();
        return ResponseEntity.ok(Map.of(
            "employeeCode", u.getEmployeeCode(),
            "email",        u.getUsername(),
            "role",         u.getRole(),
            "status",       Boolean.TRUE.equals(u.getIsAlive()) ? "Active" : "Inactive",
            "isAlive",      Boolean.TRUE.equals(u.getIsAlive())
        ));
    }

    private ResponseEntity<Map<String, Object>> toggleStatus(String code, boolean active) {
        Optional<User> opt = userRepository.findByEmployeeCode(code);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User user = opt.get();
        user.setIsAlive(active);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
            "employeeCode", user.getEmployeeCode(),
            "status",       active ? "Active" : "Inactive",
            "message",      "Account " + (active ? "activated" : "deactivated") + " successfully."
        ));
    }
}
