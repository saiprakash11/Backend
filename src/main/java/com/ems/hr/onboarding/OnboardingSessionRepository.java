package com.ems.hr.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OnboardingSessionRepository extends JpaRepository<OnboardingSession, Long> {

    Optional<OnboardingSession> findBySessionId(String sessionId);

    Optional<OnboardingSession> findTopByEmployeeCodeAndStatusOrderByUpdatedAtDesc(String employeeCode, String status);

    Optional<OnboardingSession> findTopByEmployeeCodeOrderByUpdatedAtDesc(String employeeCode);
}
