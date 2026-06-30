package com.ems.hr.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRegularizationRequestRepository extends JpaRepository<AttendanceRegularizationRequest, Long> {

    List<AttendanceRegularizationRequest> findAllByOrderByCreatedAtDesc();
}
