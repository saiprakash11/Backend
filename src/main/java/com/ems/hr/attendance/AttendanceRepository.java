package com.ems.hr.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByDate(String date);
    List<Attendance> findByEmployeeId(String employeeId);
}
