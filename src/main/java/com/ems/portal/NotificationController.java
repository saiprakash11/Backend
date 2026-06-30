package com.ems.portal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping("/unread")
    public ResponseEntity<?> unread(Authentication authentication) {
        String employeeCode = requireEmployeeCode(authentication);
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        return ResponseEntity.ok(Map.of("count", service.unreadCount(employeeCode)));
    }

    @GetMapping
    public ResponseEntity<?> list(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String employeeCode = requireEmployeeCode(authentication);
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        return ResponseEntity.ok(service.list(employeeCode, page, size));
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest(Authentication authentication) {
        String employeeCode = requireEmployeeCode(authentication);
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        return ResponseEntity.ok(service.latest(employeeCode, 5));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(Authentication authentication, @PathVariable String id) {
        String employeeCode = requireEmployeeCode(authentication);
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        return ResponseEntity.ok(service.markRead(employeeCode, id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead(Authentication authentication) {
        String employeeCode = requireEmployeeCode(authentication);
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        service.markAllRead(employeeCode);
        return ResponseEntity.ok(Map.of("success", true));
    }

    private String requireEmployeeCode(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        return authentication.getName();
    }
}
