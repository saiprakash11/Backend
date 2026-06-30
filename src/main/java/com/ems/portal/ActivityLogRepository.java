package com.ems.portal;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByEmployeeCodeOrderByCreatedAtDesc(String employeeCode);
}
