package com.ems.hr.attendance;

import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeStore;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class AttendanceService {

    private final AttendanceStore store;
    private final EmployeeStore employeeStore;

    @PersistenceContext
    private EntityManager em;

    public AttendanceService(AttendanceStore store, EmployeeStore employeeStore) {
        this.store = store;
        this.employeeStore = employeeStore;
    }

    public String markAttendance(Map<String, String> request) {
        String employeeId = request.get("employeeId");
        String dateStr    = request.get("date");
        String status     = request.get("status");

        if (employeeId == null || dateStr == null || status == null) {
            return "Missing required fields: employeeId, date, status";
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }

        if (store.alreadyMarked(employeeId, date)) {
            return "Attendance already marked for " + employeeId + " on " + date;
        }

        Employee emp = employeeStore.findById(employeeId);
        String name  = emp != null ? emp.getName() : "Unknown";

        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeCode(employeeId);
        record.setWorkDate(date);
        record.setStatus(normalizeStatus(status));
        store.add(record);
        return "Attendance marked successfully";
    }

    public List<AttendanceRecord> getByDate(String dateStr) {
        LocalDate date = parseDate(dateStr);
        return date != null ? store.getByDate(date) : List.of();
    }

    public List<AttendanceRecord> getByEmployee(String employeeId) {
        return store.getByEmployee(employeeId);
    }

    public List<AttendanceRecord> getAll() {
        return store.getAll();
    }

    public AttendanceRecord updateStatus(String id, String status) {
        Long recordId = parseId(id);
        if (recordId == null) return null;
        AttendanceRecord record = store.findById(recordId);
        if (record == null || status == null || status.isBlank()) return null;
        record.setStatus(normalizeStatus(status));
        return store.save(record);
    }

    public Map<String, Object> getStatistics(String fromDate, String toDate, String department, String employeeCode) {
        StringBuilder sql = new StringBuilder("""
                select ar.status, count(*) as total
                from attendance_records ar
                left join employee_profiles ep on ep.employee_code = ar.employee_code
                where 1=1
                """);
        List<Object> params = new ArrayList<>();

        LocalDate from = parseDate(fromDate);
        LocalDate to = parseDate(toDate);
        if (from != null) {
            sql.append(" and ar.work_date >= ?");
            params.add(java.sql.Date.valueOf(from));
        }
        if (to != null) {
            sql.append(" and ar.work_date <= ?");
            params.add(java.sql.Date.valueOf(to));
        }
        if (department != null && !department.isBlank()) {
            sql.append(" and lower(coalesce(ep.department, '')) = lower(?)");
            params.add(department.trim());
        }
        if (employeeCode != null && !employeeCode.isBlank()) {
            sql.append(" and ar.employee_code = ?");
            params.add(employeeCode.trim());
        }
        sql.append(" group by ar.status");

        List<?> rows = em.createNativeQuery(sql.toString())
            .unwrap(org.hibernate.query.NativeQuery.class)
            .setTupleTransformer((tuples, aliases) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("status", tuples[0]);
                map.put("total", tuples[1]);
                return map;
            })
            .getResultList();

        Map<String, Long> counts = new HashMap<>();
        for (Object row : rows) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) row;
            counts.put(String.valueOf(m.get("status")), ((Number) m.get("total")).longValue());
        }

        long present = counts.getOrDefault("Present", 0L) + counts.getOrDefault("On Time", 0L);
        long absent = counts.getOrDefault("Absent", 0L);
        long late = counts.getOrDefault("Late", 0L);
        long total = present + absent + late;
        double percentage = total == 0 ? 0.0 : (present * 100.0) / total;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("presentCount", present);
        stats.put("absentCount", absent);
        stats.put("lateCount", late);
        stats.put("attendancePercentage", Math.round(percentage * 10.0) / 10.0);
        stats.put("totalCount", total);
        stats.put("fromDate", from == null ? null : from.toString());
        stats.put("toDate", to == null ? null : to.toString());
        stats.put("department", department);
        stats.put("employeeCode", employeeCode);
        return stats;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Long parseId(String id) {
        if (id == null || id.isBlank()) return null;
        String digits = id.replaceAll("\\D+", "");
        if (digits.isBlank()) return null;
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) return "Present";
        String candidate = status.trim();
        if (candidate.equalsIgnoreCase("On Time")) return "Present";
        return candidate;
    }
}
