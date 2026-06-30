package com.ems.hr.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ManagerFeedbackRepository extends JpaRepository<ManagerFeedback, Long> {

    List<ManagerFeedback> findTop2ByEmployeeCodeOrderByReviewDateDesc(String employeeCode);

    List<ManagerFeedback> findByEmployeeCodeOrderByReviewDateDesc(String employeeCode);

    List<ManagerFeedback> findByEmployeeCodeOrderByCreatedAtDesc(String employeeCode);

    @Query("SELECT AVG(f.ratingScore) FROM ManagerFeedback f WHERE f.employeeCode = :employeeCode AND f.ratingScore IS NOT NULL")
    Double averageRatingByEmployeeCode(@Param("employeeCode") String employeeCode);
}
