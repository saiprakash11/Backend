package com.ems.hr.leave;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<LeaveType> findByTypeCodeIgnoreCase(String typeCode);
    Optional<LeaveType> findByTypeNameIgnoreCase(String typeName);
}
