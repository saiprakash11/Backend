package com.ems.employee.repository;

import com.ems.employee.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeProfileRepository
        extends JpaRepository<EmployeeProfile, Integer> {

    EmployeeProfile findByEmployeeCode(String employeeCode);

    int countByDateOfJoiningAfter(LocalDate date);

    @Query("SELECT ep.fullName FROM EmployeeProfile ep WHERE ep.employeeCode = :code")
    String findFullNameByEmployeeCode(@Param("code") String employeeCode);

    @Query("SELECT ep.employeeCode FROM EmployeeProfile ep WHERE LOWER(ep.department) = LOWER(:dept)")
    List<String> findEmployeeCodesByDepartment(@Param("dept") String department);
}
