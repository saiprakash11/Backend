package com.ems.hr.onboarding;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OnboardingTrackingRepository extends JpaRepository<OnboardingTracking, Long> {
    Optional<OnboardingTracking> findByEmployeeCode(String employeeCode);
    List<OnboardingTracking> findByStatus(String status);
    List<OnboardingTracking> findByDepartment(String department);

    @Query("SELECT o FROM OnboardingTracking o WHERE " +
           "LOWER(o.employeeName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(o.employeeCode) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(o.department) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<OnboardingTracking> search(@Param("q") String query);
}
