package com.ems.employee.service;

import com.ems.employee.entity.EmployeeProfile;
import com.ems.employee.repository.EmployeeProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProfileService {

    @Autowired
    EmployeeProfileRepository repository;

    public EmployeeProfile getProfile(String employeeCode) {
        return repository.findByEmployeeCode(employeeCode);
    }

    public EmployeeProfile save(EmployeeProfile profile) {
        return repository.save(profile);
    }
}