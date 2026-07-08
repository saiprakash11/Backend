package com.ems.hr.onboarding;

import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OnboardingService {

    private final OnboardingSessionRepository sessionRepository;
    private final EmployeeStore employeeStore;
    private final ObjectMapper objectMapper;

    OnboardingService(
            OnboardingSessionRepository sessionRepository,
            EmployeeStore employeeStore,
            ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.employeeStore = employeeStore;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> saveStep(Map<String, String> body) {
        OnboardingSession session = resolveSession(body.get("sessionId"));
        Map<String, String> data = readSessionData(session);

        Map<String, String> stepData = new HashMap<>(body);
        stepData.remove("sessionId");
        stepData.remove("step");
        data.putAll(stepData);

        int requestedStep = parseInt(body.get("step"), session.getCurrentStep());
        session.setCurrentStep(Math.max(session.getCurrentStep(), requestedStep + 1));
        session.setEmployeeCode(blankToNull(body.get("employeeCode")));
        session.setSessionDataJson(writeSessionData(data));
        session.setStatus("ACTIVE");
        session.setLastStepAt(LocalDateTime.now());
        sessionRepository.save(session);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", session.getSessionId());
        response.put("nextStep", session.getCurrentStep());
        response.put("message", "Step saved successfully");
        return response;
    }

    public Map<String, Object> complete(String sessionId) {
        OnboardingSession session = sessionRepository.findBySessionId(sessionId).orElse(null);
        if (session == null) {
            return Map.of("error", "Session not found: " + sessionId);
        }

        Map<String, String> data = readSessionData(session);
        String newId = employeeStore.nextId();
        double salary;
        try {
            salary = Double.parseDouble(data.getOrDefault("salary", "50000"));
        } catch (NumberFormatException ex) {
            return Map.of("error", "Invalid salary: " + data.get("salary"));
        }

        Employee employee = new Employee(
                newId,
                data.getOrDefault("name", "Unknown"),
                data.getOrDefault("email", "unknown@company.com"),
                data.getOrDefault("phone", "0000000000"),
                data.getOrDefault("department", "General"),
                data.getOrDefault("designation", "Employee"),
                data.getOrDefault("joinDate", LocalDate.now().toString()),
                "Active",
                salary
        );

        employeeStore.add(employee);

        session.setStatus("COMPLETED");
        session.setCurrentStep(Math.max(session.getCurrentStep(), 5));
        session.setEmployeeCode(newId);
        session.setSessionDataJson(writeSessionData(data));
        session.setLastStepAt(LocalDateTime.now());
        sessionRepository.save(session);

        return Map.of(
                "message", "Employee onboarded successfully",
                "employeeId", newId,
                "name", employee.getName()
        );
    }

    public OnboardingSession getSession(String sessionId) {
        return sessionRepository.findBySessionId(sessionId).orElse(null);
    }

    public OnboardingSession recoverSession(String employeeCode) {
        return sessionRepository.findTopByEmployeeCodeAndStatusOrderByUpdatedAtDesc(employeeCode, "ACTIVE")
                .orElseGet(() -> sessionRepository.findTopByEmployeeCodeOrderByUpdatedAtDesc(employeeCode).orElse(null));
    }

    private OnboardingSession resolveSession(String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            return sessionRepository.findBySessionId(sessionId).orElseGet(() -> createSession(sessionId));
        }
        return createSession(null);
    }

    private OnboardingSession createSession(String sessionId) {
        OnboardingSession session = new OnboardingSession();
        session.setSessionId(sessionId == null || sessionId.isBlank()
                ? UUID.randomUUID().toString().substring(0, 12)
                : sessionId);
        session.setCurrentStep(1);
        session.setSessionDataJson("{}");
        session.setStatus("ACTIVE");
        session.setLastStepAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    private Map<String, String> readSessionData(OnboardingSession session) {
        String json = session.getSessionDataJson();
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            return new HashMap<>();
        }
    }

    private String writeSessionData(Map<String, String> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            return "{}";
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
// NOTE: OnboardingController was moved to com.ems.controllers.AllControllers.java

