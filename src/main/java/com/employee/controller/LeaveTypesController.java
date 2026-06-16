package com.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides leave type definitions consumed by the Apply Leave page.
 * GET /api/leave/types
 *
 * Queries the `leave_types` table if it exists; falls back to a sensible
 * default list so the frontend form always has options to show.
 */
@RestController
@RequestMapping("/api/leave")
public class LeaveTypesController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/types")
    public ResponseEntity<List<Map<String, Object>>> getLeaveTypes() {
        try {
            List<Map<String, Object>> types = jdbcTemplate.queryForList(
                    "select type_code, type_name, annual_quota, is_paid, description " +
                    "from leave_types " +
                    "order by type_name"
            );
            if (!types.isEmpty()) {
                return ResponseEntity.ok(types);
            }
        } catch (Exception ignored) {
            // leave_types table may not exist — fall through to defaults
        }

        /* Default leave types — always available even without a leave_types table */
        return ResponseEntity.ok(defaultLeaveTypes());
    }

    private List<Map<String, Object>> defaultLeaveTypes() {
        return Arrays.asList(
                leaveType("CL",  "Casual Leave",    12,   true,  "Short-notice personal leave"),
                leaveType("SL",  "Sick Leave",       12,   true,  "Medical or health-related leave"),
                leaveType("PL",  "Privilege Leave",  15,   true,  "Planned annual leave"),
                leaveType("LOP", "Loss of Pay",      null, false, "Unpaid leave when balance is exhausted")
        );
    }

    private Map<String, Object> leaveType(String code, String name, Integer quota, boolean isPaid, String desc) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("type_code",    code);
        m.put("typeName",     name);
        m.put("typeCode",     code);
        m.put("annual_quota", quota);
        m.put("annualQuota",  quota);
        m.put("is_paid",      isPaid);
        m.put("isPaid",       isPaid);
        m.put("description",  desc);
        return m;
    }
}
