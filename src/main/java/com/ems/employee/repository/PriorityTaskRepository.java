package com.ems.employee.repository;

import com.ems.employee.entity.PriorityTask;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriorityTaskRepository extends JpaRepository<PriorityTask, Long> {
    List<PriorityTask> findTop3ByEmployeeCodeOrderByDueDateAsc(String employeeCode);
}
