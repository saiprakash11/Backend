package com.ems.hr.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByEmployeeCodeAndWorkDate(String employeeCode, LocalDate workDate);

    List<AttendanceRecord> findByEmployeeCodeAndWorkDateGreaterThanEqualOrderByWorkDateDesc(String employeeCode, LocalDate workDate);

    List<AttendanceRecord> findByEmployeeCodeAndStatusNotOrderByWorkDateDesc(String employeeCode, String status);

    List<AttendanceRecord> findByWorkDate(LocalDate workDate);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.workDate = :date AND LOWER(a.status) IN ('present', 'late', 'on time')")
    long countPresentOnDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(a.totalHours), 0) FROM AttendanceRecord a WHERE a.employeeCode = :code AND a.workDate >= :start")
    BigDecimal totalHoursSince(@Param("code") String employeeCode, @Param("start") LocalDate start);
}
