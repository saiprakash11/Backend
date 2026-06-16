package com.employee.controller;

import com.employee.entity.EmployeeProfile;
import com.employee.service.EmployeeProfileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-profiles")
public class EmployeeProfileController {

    @Autowired
    EmployeeProfileService service;

    @GetMapping("/{employeeCode}")
    public EmployeeProfile getProfile(
            @PathVariable String employeeCode
    ) {

        return service.getProfile(employeeCode);
    }

    @PostMapping
    public EmployeeProfile save(
            @RequestBody EmployeeProfile profile
    ) {

        return service.save(profile);
    }
}
