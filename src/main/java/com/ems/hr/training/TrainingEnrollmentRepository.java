package com.ems.hr.training;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TrainingEnrollmentRepository extends JpaRepository<TrainingEnrollment, Long> {

    List<TrainingEnrollment> findByEmployeeCodeOrderByEnrolledAtDesc(String employeeCode);

    Optional<TrainingEnrollment> findByCourseIdAndEmployeeCode(Long courseId, String employeeCode);
}
