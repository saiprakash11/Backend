package com.ems.hr.common;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class EmployeeStore {
    private final EmployeeRepository repository;

    public EmployeeStore(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAll() { return repository.findAll(); }
    public Employee findById(String id) { return repository.findById(id).orElse(null); }
    public void add(Employee employee) { repository.save(employee); }
    public String nextId() {
        int next = repository.findAll().stream()
                .map(Employee::getId)
                .mapToInt(this::extractSequence)
                .max()
                .orElse(0) + 1;
        return "E" + String.format("%03d", next);
    }
    public boolean replace(String id, Employee employee) {
        if(repository.existsById(id)) {
            employee.setId(id);
            repository.save(employee);
            return true;
        }
        return false;
    }
    public boolean delete(String id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private int extractSequence(String id) {
        if (id == null) {
            return 0;
        }

        String digits = id.replaceAll("\\D+", "");
        if (digits.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
