package com.ems.hr.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeLeaveRequestRepository extends JpaRepository<EmployeeLeaveRequest, String> {

    List<EmployeeLeaveRequest> findByEmployeeCodeOrderByCreatedAtDesc(String employeeCode);

    @Query("SELECT COALESCE(SUM(r.numberOfDays), 0) FROM EmployeeLeaveRequest r WHERE r.employeeCode = :employeeCode AND r.status = 'Pending'")
    Long sumPendingDaysByEmployeeCode(@Param("employeeCode") String employeeCode);

    @Query("SELECT COUNT(r) FROM EmployeeLeaveRequest r WHERE r.status = 'Approved' AND :date BETWEEN r.startDate AND r.endDate")
    long countApprovedOnDate(@Param("date") LocalDate date);
}
