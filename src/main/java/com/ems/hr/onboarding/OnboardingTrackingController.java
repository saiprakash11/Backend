package com.ems.hr.onboarding;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * OnboardingTrackingController – full CRUD for onboarding process tracking.
 *
 * GET    /api/onboarding/tracking              → list all
 * GET    /api/onboarding/tracking/search?q=... → search
 * GET    /api/onboarding/tracking/{id}         → get one
 * POST   /api/onboarding/tracking              → create
 * PUT    /api/onboarding/tracking/{id}         → update
 * PUT    /api/onboarding/tracking/{id}/step/{n}→ mark step done
 * PUT    /api/onboarding/tracking/{id}/complete→ mark all done
 */
@RestController
@RequestMapping("/api/onboarding/tracking")
public class OnboardingTrackingController {

    private final OnboardingTrackingRepository repo;

    public OnboardingTrackingController(OnboardingTrackingRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<OnboardingTracking> list() {
        return repo.findAll();
    }

    @GetMapping("/search")
    public List<OnboardingTracking> search(@RequestParam String q) {
        return repo.search(q);
    }

    @GetMapping("/status/{status}")
    public List<OnboardingTracking> byStatus(@PathVariable String status) {
        return repo.findByStatus(status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OnboardingTracking> getOne(@PathVariable Long id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OnboardingTracking> create(@RequestBody OnboardingTracking body) {
        body.recalcProgress();
        return ResponseEntity.ok(repo.save(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OnboardingTracking> update(
            @PathVariable Long id, @RequestBody OnboardingTracking body) {
        return repo.findById(id).map(existing -> {
            existing.setEmployeeName(body.getEmployeeName());
            existing.setDepartment(body.getDepartment());
            existing.setJoiningDate(body.getJoiningDate());
            existing.setAssignedHr(body.getAssignedHr());
            existing.setNotes(body.getNotes());
            existing.setStep1Done(body.getStep1Done());
            existing.setStep2Done(body.getStep2Done());
            existing.setStep3Done(body.getStep3Done());
            existing.setStep4Done(body.getStep4Done());
            existing.setStep5Done(body.getStep5Done());
            existing.recalcProgress();
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Mark a specific step (1–5) as done */
    @PutMapping("/{id}/step/{stepNum}")
    public ResponseEntity<OnboardingTracking> markStep(
            @PathVariable Long id, @PathVariable int stepNum) {
        return repo.findById(id).map(ob -> {
            switch (stepNum) {
                case 1 -> ob.setStep1Done(true);
                case 2 -> ob.setStep2Done(true);
                case 3 -> ob.setStep3Done(true);
                case 4 -> ob.setStep4Done(true);
                case 5 -> ob.setStep5Done(true);
            }
            ob.recalcProgress();
            return ResponseEntity.ok(repo.save(ob));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** Mark all steps done → Completed */
    @PutMapping("/{id}/complete")
    public ResponseEntity<OnboardingTracking> complete(@PathVariable Long id) {
        return repo.findById(id).map(ob -> {
            ob.setStep1Done(true); ob.setStep2Done(true); ob.setStep3Done(true);
            ob.setStep4Done(true); ob.setStep5Done(true);
            ob.setStatus("Completed");
            ob.recalcProgress();
            return ResponseEntity.ok(repo.save(ob));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Onboarding record deleted."));
    }
}
