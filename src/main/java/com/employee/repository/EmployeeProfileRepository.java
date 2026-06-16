package com.employee.repository;

import com.employee.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeProfileRepository
        extends JpaRepository<EmployeeProfile, Integer> {

    EmployeeProfile findByEmployeeCode(String employeeCode);
}
