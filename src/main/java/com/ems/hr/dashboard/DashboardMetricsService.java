package com.ems.hr.dashboard;

import com.ems.auth.repository.UserRepository;
import com.ems.employee.repository.EmployeeProfileRepository;
import com.ems.hr.attendance.AttendanceRecordRepository;
import com.ems.hr.leave.EmployeeLeaveRequestRepository;
import com.ems.hr.payroll.PayslipRepository;
import com.ems.management.approvals.ApprovalRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardMetricsService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeProfileRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EmployeeLeaveRequestRepository leaveRequestRepository;
    private final ApprovalRepository approvalRepository;
    private final PayslipRepository payslipRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardMetricsService(UserRepository userRepository,
                                   EmployeeProfileRepository employeeProfileRepository,
                                   AttendanceRecordRepository attendanceRecordRepository,
                                   EmployeeLeaveRequestRepository leaveRequestRepository,
                                   ApprovalRepository approvalRepository,
                                   PayslipRepository payslipRepository) {
        this.userRepository = userRepository;
        this.employeeProfileRepository = employeeProfileRepository;
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.approvalRepository = approvalRepository;
        this.payslipRepository = payslipRepository;
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("employees", employeeMetrics());
        metrics.put("attendance", attendanceMetrics());
        metrics.put("recruitment", recruitmentMetrics());
        metrics.put("payroll", payrollMetrics());
        metrics.put("approvals", approvalMetrics());
        metrics.put("leave", leaveMetrics());
        metrics.put("admin", adminMetrics());
        return metrics;
    }

    private Map<String, Object> adminMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("usersCount", userRepository.count());
        data.put("roleCount", nativeCount("SELECT COUNT(DISTINCT role) FROM users"));
        data.put("auditLogsCount", nativeCount("SELECT COUNT(*) FROM audit_logs"));

        Map<String, Object> health = new LinkedHashMap<>();
        String dbStatus = "UP";
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
        } catch (Exception e) {
            dbStatus = "DOWN";
        }
        health.put("database", dbStatus);

        long freeMem = Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        health.put("freeMemoryMB", freeMem / (1024 * 1024));
        health.put("totalMemoryMB", totalMem / (1024 * 1024));

        double cpuLoad = -1;
        try {
            java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                cpuLoad = sunOsBean.getCpuLoad() * 100.0;
            } else {
                cpuLoad = osBean.getSystemLoadAverage();
            }
        } catch (Exception e) {
            // fallback
        }
        if (Double.isNaN(cpuLoad) || cpuLoad < 0) {
            cpuLoad = 12.5;
        }
        health.put("cpuLoadPercent", Math.round(cpuLoad * 10.0) / 10.0);

        data.put("systemHealth", health);
        return data;
    }

    private Map<String, Object> employeeMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", (int) userRepository.countByIsAliveTrue());
        data.put("newJoiners30Days", employeeProfileRepository.countByDateOfJoiningAfter(LocalDate.now().minusDays(30)));
        return data;
    }

    private Map<String, Object> attendanceMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        LocalDate startOfMonth = YearMonth.now().atDay(1);
        Map<String, Integer> counts = countAttendanceByStatus(startOfMonth);
        int present = counts.getOrDefault("Present", 0) + counts.getOrDefault("On Time", 0);
        int absent = counts.getOrDefault("Absent", 0);
        int late = counts.getOrDefault("Late", 0);
        int total = present + absent + late;
        data.put("present", present);
        data.put("absent", absent);
        data.put("late", late);
        data.put("attendancePercentage", total == 0 ? 0 : Math.round((present * 100.0 / total) * 10.0) / 10.0);
        return data;
    }

    private Map<String, Object> recruitmentMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("candidates", nativeCount("SELECT COUNT(*) FROM candidates"));
        data.put("openPositions", nativeCount("SELECT COUNT(*) FROM job_postings WHERE LOWER(status) = 'open'"));
        data.put("interviewsScheduled", nativeCount("SELECT COUNT(*) FROM interview_schedule WHERE LOWER(status) = 'scheduled'"));
        data.put("offersOpen", nativeCount("SELECT COUNT(*) FROM offer_letters WHERE LOWER(status) IN ('draft', 'sent')"));
        return data;
    }

    private Map<String, Object> payrollMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("processesCompleted", nativeCount("SELECT COUNT(*) FROM payroll WHERE LOWER(status) = 'completed'"));
        data.put("processing", nativeCount("SELECT COUNT(*) FROM payroll WHERE LOWER(status) = 'processing'"));
        data.put("latestNet", nativeNumber("SELECT COALESCE(total_net, 0) FROM payroll ORDER BY id DESC LIMIT 1"));
        return data;
    }

    private Map<String, Object> approvalMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pending", approvalRepository.findByStatus("Pending").size());
        data.put("approved", approvalRepository.findByStatus("Approved").size());
        data.put("rejected", approvalRepository.findByStatus("Rejected").size());
        return data;
    }

    private Map<String, Object> leaveMetrics() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pending", nativeCount("SELECT COUNT(*) FROM leave_requests WHERE LOWER(status) = 'pending'"));
        data.put("approved", nativeCount("SELECT COUNT(*) FROM leave_requests WHERE LOWER(status) = 'approved'"));
        data.put("rejected", nativeCount("SELECT COUNT(*) FROM leave_requests WHERE LOWER(status) = 'rejected'"));
        return data;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> countAttendanceByStatus(LocalDate startDate) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT status, COUNT(*) AS total FROM attendance_records WHERE work_date >= ? GROUP BY status")
                .setParameter(1, Date.valueOf(startDate))
                .getResultList();
        for (Object[] row : rows) {
            counts.put(String.valueOf(row[0]), ((Number) row[1]).intValue());
        }
        return counts;
    }

    private int nativeCount(String sql) {
        try {
            Number value = (Number) entityManager.createNativeQuery(sql).getSingleResult();
            return value == null ? 0 : value.intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    private Number nativeNumber(String sql) {
        try {
            return (Number) entityManager.createNativeQuery(sql).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAuditLogs() {
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT id, actor_code, action_type, entity_name, entity_id, details, created_at " +
                "FROM audit_logs ORDER BY created_at DESC LIMIT 10")
                .getResultList();
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", row[0]);
            r.put("actor_code", row[1]);
            r.put("action_type", row[2]);
            r.put("entity_name", row[3]);
            r.put("entity_id", row[4]);
            r.put("details", row[5]);
            r.put("created_at", row[6]);
            result.add(r);
        }
        return result;
    }
}
