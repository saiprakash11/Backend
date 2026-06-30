package com.ems.hr.attendance;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class AttendanceStore {

    private final AttendanceRecordRepository repository;

    public AttendanceStore(AttendanceRecordRepository repository) {
        this.repository = repository;
    }

    public List<AttendanceRecord> getAll() {
        return repository.findAll();
    }

    public void add(AttendanceRecord record) {
        repository.save(record);
    }

    public AttendanceRecord save(AttendanceRecord record) {
        return repository.save(record);
    }

    public AttendanceRecord findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public boolean alreadyMarked(String employeeId, LocalDate date) {
        return repository.findByEmployeeCodeAndWorkDate(employeeId, date).isPresent();
    }

    public int countPresentOnDate(LocalDate date) {
        return (int) repository.countPresentOnDate(date);
    }

    public List<AttendanceRecord> getByDate(LocalDate date) {
        return repository.findByWorkDate(date);
    }

    public List<AttendanceRecord> getByEmployee(String employeeId) {
        return repository.findByEmployeeCodeAndWorkDateGreaterThanEqualOrderByWorkDateDesc(
            employeeId, LocalDate.of(2020, 1, 1));
    }
}
