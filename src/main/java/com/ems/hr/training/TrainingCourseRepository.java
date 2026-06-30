package com.ems.hr.training;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrainingCourseRepository extends JpaRepository<TrainingCourse, Long> {

    List<TrainingCourse> findByStatusOrderByCreatedAtDesc(String status);

    List<TrainingCourse> findAllByOrderByCreatedAtDesc();
}
