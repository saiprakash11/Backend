package com.ems.hr.leave;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

@Component
public class LeaveStore {
    private final LeaveRepository repository;

    public LeaveStore(LeaveRepository repository) {
        this.repository = repository;
    }

    public List<LeaveRequest> getAll() { return repository.findAll(); }
    public String nextId() { return UUID.randomUUID().toString(); }
    public void add(LeaveRequest req) { repository.save(req); }
    public LeaveRequest save(LeaveRequest req) { return repository.save(req); }
    public LeaveRequest findById(String id) { return repository.findById(id).orElse(null); }
    public int countApprovedLeavesToday(String today) {
        LocalDate todayDate = LocalDate.parse(today);
        return (int) repository.findAll().stream()
                .filter(r -> r.getStatus().equals("Approved"))
                .filter(r -> {
                    LocalDate from = LocalDate.parse(r.getFromDate());
                    LocalDate to = LocalDate.parse(r.getToDate());
                    return !todayDate.isBefore(from) && !todayDate.isAfter(to);
                }).count();
    }
}
