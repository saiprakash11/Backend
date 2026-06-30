package com.ems.hr.dashboard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService service;

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(service.getSummary());
    }

    @GetMapping("/reports/attendance")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceReport(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return ResponseEntity.ok(queryAttendanceByDept(department));
        }
        return ResponseEntity.ok(queryAttendanceAll());
    }

    @GetMapping("/reports/leave")
    public ResponseEntity<List<Map<String, Object>>> getLeaveReport() {
        return ResponseEntity.ok(queryLeaveReport());
    }

    @GetMapping("/dashboard/attendance-statistics")
    public ResponseEntity<List<Map<String, Object>>> getAttendanceStatistics(@RequestParam(required = false) String department) {
        if (department != null && !department.isEmpty()) {
            return ResponseEntity.ok(queryAttendanceStatsByDept(department));
        }
        return ResponseEntity.ok(queryAttendanceStatsAll());
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryAttendanceAll() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT DATE_FORMAT(work_date, '%Y-%m') AS month, " +
                "SUM(CASE WHEN status IN ('Present','Late') THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN status = 'Absent' THEN 1 ELSE 0 END) AS absentCount " +
                "FROM attendance_records " +
                "WHERE work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH) " +
                "GROUP BY month ORDER BY month").getResultList();
        return mapRows(rows, "month", "presentCount", "absentCount");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryAttendanceByDept(String department) {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT DATE_FORMAT(ar.work_date, '%Y-%m') AS month, " +
                "SUM(CASE WHEN ar.status IN ('Present','Late') THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount " +
                "FROM attendance_records ar " +
                "JOIN employee_profiles ep ON ar.employee_code = ep.employee_code " +
                "WHERE ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 12 MONTH) " +
                "AND ep.department = ? " +
                "GROUP BY month ORDER BY month")
                .setParameter(1, department).getResultList();
        return mapRows(rows, "month", "presentCount", "absentCount");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryLeaveReport() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT leave_type AS type, COUNT(*) AS count " +
                "FROM leave_requests WHERE status = 'Approved' GROUP BY leave_type").getResultList();
        return mapRows(rows, "type", "count");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryAttendanceStatsAll() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT ep.department, COUNT(DISTINCT ep.employee_code) AS headcount, " +
                "SUM(CASE WHEN ar.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                "SUM(CASE WHEN ar.status = 'Late' THEN 1 ELSE 0 END) AS lateCount " +
                "FROM employee_profiles ep " +
                "LEFT JOIN attendance_records ar ON ep.employee_code = ar.employee_code " +
                "AND ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " +
                "GROUP BY ep.department ORDER BY ep.department").getResultList();
        return mapRows(rows, "department", "headcount", "presentCount", "absentCount", "lateCount");
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> queryAttendanceStatsByDept(String department) {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT ep.department, COUNT(DISTINCT ep.employee_code) AS headcount, " +
                "SUM(CASE WHEN ar.status = 'Present' THEN 1 ELSE 0 END) AS presentCount, " +
                "SUM(CASE WHEN ar.status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, " +
                "SUM(CASE WHEN ar.status = 'Late' THEN 1 ELSE 0 END) AS lateCount " +
                "FROM employee_profiles ep " +
                "LEFT JOIN attendance_records ar ON ep.employee_code = ar.employee_code " +
                "AND ar.work_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 30 DAY) " +
                "WHERE ep.department = ? " +
                "GROUP BY ep.department ORDER BY ep.department")
                .setParameter(1, department).getResultList();
        return mapRows(rows, "department", "headcount", "presentCount", "absentCount", "lateCount");
    }

    private List<Map<String, Object>> mapRows(List<Object[]> rows, String... labels) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < labels.length && i < row.length; i++) {
                map.put(labels[i], row[i]);
            }
            result.add(map);
        }
        return result;
    }
}
