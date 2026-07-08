package com.ems.hr.recruitment;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RecruitmentService {

    private final RecruitmentRepository repository;

    RecruitmentService(RecruitmentRepository repository) {
        this.repository = repository;
    }

    public List<RecruitmentCandidate> getAll() {
        return repository.findAll();
    }

    public RecruitmentCandidate addCandidate(RecruitmentCandidate candidate) {
        candidate.setId(generateNextId());
        if (candidate.getStatus() == null || candidate.getStatus().isBlank()) {
            candidate.setStatus("Applied");
        }
        if (candidate.getAppliedDate() == null) {
            candidate.setAppliedDate(LocalDate.now());
        }
        return repository.save(candidate);
    }

    public RecruitmentCandidate updateStatus(String id, String status) {
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
// NOTE: RecruitmentController was moved to com.ems.controllers.AllControllers.java

