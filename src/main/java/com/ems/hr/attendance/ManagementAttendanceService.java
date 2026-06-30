package com.ems.hr.attendance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class ManagementAttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ManagementAttendanceService(AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public Map<String, Object> getDashboardMetrics() {
        LocalDate today = LocalDate.now();
        List<AttendanceRecord> records = attendanceRecordRepository.findByWorkDate(today);
        int present = 0, absent = 0, late = 0, onLeave = 0;
        for (AttendanceRecord r : records) {
            switch (r.getStatus() != null ? r.getStatus() : "") {
                case "Present" -> present++;
                case "Absent" -> absent++;
                case "Late" -> late++;
                case "On Leave" -> onLeave++;
            }
        }
        int total = records.size();
        double percentage = total > 0 ? Math.round((present * 100.0 / total) * 10.0) / 10.0 : 0.0;

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("presentCount", present);
        metrics.put("absentCount", absent);
        metrics.put("lateCount", late);
        metrics.put("onLeaveCount", onLeave);
        metrics.put("totalCount", total);
        metrics.put("attendancePercentage", percentage);
        return metrics;
    }

    public Map<String, Object> getAttendanceRecords(int page, int size, String search,
            String department, String status, String date, String team) {
        int offset = (page - 1) * size;

        StringBuilder countSql = new StringBuilder(
                "SELECT COUNT(*) FROM attendance_records ar " +
                "LEFT JOIN employee_profiles ep ON ep.employee_code = ar.employee_code WHERE 1=1");
        StringBuilder dataSql = new StringBuilder(
                "SELECT ar.id, ar.employee_code, ar.work_date, ar.check_in, ar.check_out, " +
                "ar.status, ar.total_hours, ar.hours_worked, ep.full_name, ep.department " +
                "FROM attendance_records ar " +
                "LEFT JOIN employee_profiles ep ON ep.employee_code = ar.employee_code WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            String s = " AND (LOWER(ep.full_name) LIKE ? OR LOWER(ar.employee_code) LIKE ?)";
            countSql.append(s); dataSql.append(s);
            String sp = "%" + search.toLowerCase() + "%";
            params.add(sp); params.add(sp);
        }
        if (department != null && !department.isBlank()) {
            String s = " AND LOWER(ep.department) = LOWER(?)";
            countSql.append(s); dataSql.append(s);
            params.add(department);
        }
        if (status != null && !status.isBlank()) {
            String s = " AND ar.status = ?";
            countSql.append(s); dataSql.append(s);
            params.add(status);
        }
        if (date != null && !date.isBlank()) {
            String s = " AND ar.work_date = ?";
            countSql.append(s); dataSql.append(s);
            params.add(date);
        }

        var countQuery = entityManager.createNativeQuery(countSql.toString());
        for (int i = 0; i < params.size(); i++) {
            countQuery.setParameter(i + 1, params.get(i));
        }
        Number totalNum = (Number) countQuery.getSingleResult();
        int totalElements = totalNum == null ? 0 : totalNum.intValue();

        dataSql.append(" ORDER BY ar.work_date DESC, ar.id DESC LIMIT ? OFFSET ?");
        params.add(size); params.add(offset);

        var dataQuery = entityManager.createNativeQuery(dataSql.toString());
        for (int i = 0; i < params.size(); i++) {
            dataQuery.setParameter(i + 1, params.get(i));
        }
        @SuppressWarnings("unchecked")
        List<Object[]> rows = dataQuery.getResultList();

        List<Map<String, Object>> content = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("id", row[0]);
            r.put("employeeId", row[1]);
            r.put("date", row[2]);
            r.put("checkIn", row[3]);
            r.put("checkOut", row[4]);
            r.put("status", row[5]);
            r.put("employeeName", row[8]);
            r.put("department", row[9]);
            Object hw = row[7];
            r.put("workHours", hw != null ? hw.toString().replace(",", ".") : "0");
            content.add(r);
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", content);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);
        response.put("size", size);
        return response;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAttendanceTrends(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        List<Object[]> rows = entityManager.createNativeQuery(
                "SELECT work_date AS date, " +
                "COUNT(CASE WHEN status = 'Present' THEN 1 END) AS present, " +
                "COUNT(CASE WHEN status = 'Absent' THEN 1 END) AS absent, " +
                "COUNT(CASE WHEN status = 'Late' THEN 1 END) AS late, " +
                "COUNT(CASE WHEN status = 'On Leave' THEN 1 END) AS on_leave, " +
                "COUNT(*) AS total " +
                "FROM attendance_records WHERE work_date >= ? " +
                "GROUP BY work_date ORDER BY work_date")
                .setParameter(1, startDate).getResultList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", row[0]);
            map.put("present", row[1]);
            map.put("absent", row[2]);
            map.put("late", row[3]);
            map.put("on_leave", row[4]);
            map.put("total", row[5]);
            result.add(map);
        }
        return result;
    }
}