package com.ems.hr.recruitment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
class RecruitmentService {

    private final RecruitmentRepository repository;

    RecruitmentService(RecruitmentRepository repository) {
        this.repository = repository;
    }

    List<RecruitmentCandidate> getAll() {
        return repository.findAll();
    }

    RecruitmentCandidate addCandidate(RecruitmentCandidate candidate) {
        candidate.setId(generateNextId());
        if (candidate.getStatus() == null || candidate.getStatus().isBlank()) {
            candidate.setStatus("Applied");
        }
        if (candidate.getAppliedDate() == null) {
            candidate.setAppliedDate(LocalDate.now());
        }
        return repository.save(candidate);
    }

    RecruitmentCandidate updateStatus(String id, String status) {
        RecruitmentCandidate candidate = repository.findById(id).orElse(null);
        if (candidate == null) {
            return null;
        }
        candidate.setStatus(status);
        return repository.save(candidate);
    }

    private String generateNextId() {
        int max = repository.findAll().stream()
                .map(RecruitmentCandidate::getId)
                .mapToInt(this::extractSequence)
                .max()
                .orElse(0);
        return "R" + String.format("%03d", max + 1);
    }

    private int extractSequence(String id) {
        if (id == null) return 0;
        String digits = id.replaceAll("\\D+", "");
        if (digits.isBlank()) return 0;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}

@RestController
@RequestMapping("/api/recruitment")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
class RecruitmentController {
    private final RecruitmentService service;

    RecruitmentController(RecruitmentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RecruitmentCandidate>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<RecruitmentCandidate> add(@RequestBody @jakarta.validation.Valid RecruitmentCandidate candidate) {
        return ResponseEntity.ok(service.addCandidate(candidate));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RecruitmentCandidate> updateStatus(
            @PathVariable String id,
            @RequestParam(required = false) String status,
            @RequestBody(required = false) Map<String, String> body) {
        String nextStatus = status != null ? status : (body != null ? body.get("status") : null);
        if (nextStatus == null || nextStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        RecruitmentCandidate updated = service.updateStatus(id, normalizeStatus(nextStatus));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    private String normalizeStatus(String status) {
        String normalized = status.trim().toLowerCase().replace('_', ' ');
        return java.util.Arrays.stream(normalized.split("\\s+"))
                .filter(part -> !part.isBlank())
                .map(part -> part.substring(0, 1).toUpperCase() + part.substring(1))
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
