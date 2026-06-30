package com.ems.hr.directory;

import com.ems.hr.common.Employee;
import com.ems.hr.common.EmployeeStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class DirectoryController {

    private final EmployeeStore store;

    public DirectoryController(EmployeeStore store) {
        this.store = store;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(store.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getById(@PathVariable String id) {
        Employee employee = store.findById(id);
        if (employee == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchByName(@RequestParam String name) {
        List<Employee> result = store.getAll().stream()
                .filter(e -> e.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Employee>> filterByDepartment(@RequestParam String department) {
        List<Employee> result = store.getAll().stream()
                .filter(e -> e.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Employee employee) {
        if (employee.getId() == null || employee.getId().isBlank()) {
            employee.setId(store.nextId());
        }
        if (store.findById(employee.getId()) != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Employee already exists: " + employee.getId()));
        }
        store.add(employee);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Employee employee) {
        Employee existing = store.findById(id);
        if (existing == null) return ResponseEntity.notFound().build();

        employee.setId(id);
        store.replace(id, employee);
        return ResponseEntity.ok(employee);
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable String id) {
        Employee employee = store.findById(id);
        if (employee == null) return ResponseEntity.notFound().build();

        employee.setStatus("ACTIVE");
        store.replace(id, employee);

        return ResponseEntity.ok(Map.of(
                "message", "Employee activated",
                "employeeId", id,
                "status", employee.getStatus()
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        boolean removed = store.delete(id);
        if (!removed) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("message", "Employee deleted: " + id));
    }
}
