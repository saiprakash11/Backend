package com.ems.hr.attendance;

import com.ems.employee.repository.EmployeeProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AttendanceCorrectionService {

    private final AttendanceRegularizationRequestRepository repository;
    private final EmployeeProfileRepository employeeProfileRepository;

    public AttendanceCorrectionService(AttendanceRegularizationRequestRepository repository,
                                       EmployeeProfileRepository employeeProfileRepository) {
        this.repository = repository;
        this.employeeProfileRepository = employeeProfileRepository;
    }

    public List<Map<String, Object>> getAllCorrections() {
        List<AttendanceRegularizationRequest> requests = repository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();
        for (AttendanceRegularizationRequest r : requests) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getId());
            String empName = employeeProfileRepository.findFullNameByEmployeeCode(r.getEmployeeCode());
            map.put("employeeName", empName != null ? empName : r.getEmployeeCode());
            map.put("employeeCode", r.getEmployeeCode());
            map.put("date", r.getWorkDate());
            map.put("requestType", determineRequestType(r));
            map.put("reason", r.getReason());
            map.put("status", r.getStatus());
            map.put("checkIn", r.getRequestedCheckIn());
            map.put("checkOut", r.getRequestedCheckOut());
            map.put("createdAt", r.getCreatedAt());
            result.add(map);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> approveCorrection(Long id) {
        var opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("success", false, "error", "Correction request not found");
        }
        var req = opt.get();
        req.setStatus("Approved");
        repository.save(req);
        return Map.of("success", true, "message", "Correction approved successfully");
    }

    @Transactional
    public Map<String, Object> rejectCorrection(Long id) {
        var opt = repository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("success", false, "error", "Correction request not found");
        }
        var req = opt.get();
        req.setStatus("Rejected");
        repository.save(req);
        return Map.of("success", true, "message", "Correction rejected successfully");
    }

    @Transactional
    public int bulkApproveCorrections(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        List<AttendanceRegularizationRequest> requests = repository.findAllById(ids);
        for (var req : requests) {
            req.setStatus("Approved");
        }
        repository.saveAll(requests);
        return requests.size();
    }

    private String determineRequestType(AttendanceRegularizationRequest r) {
        boolean hasIn = r.getRequestedCheckIn() != null;
        boolean hasOut = r.getRequestedCheckOut() != null;
        if (hasIn && hasOut) return "Missing Punch";
        if (hasIn) return "Late Arrival";
        if (hasOut) return "Early Departure";
        return "Other";
    }
}