package com.ems.employee.repository;

import com.ems.employee.entity.EmployeeProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeProfilePhotoRepository extends JpaRepository<EmployeeProfilePhoto, Long> {
    Optional<EmployeeProfilePhoto> findTopByEmployeeCodeAndIsActiveTrueOrderByUploadedAtDesc(String employeeCode);

    void deleteByEmployeeCode(String employeeCode);
}
