package com.ems.hr.training;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<TrainingCourse>> getAllCourses() {
        return ResponseEntity.ok(trainingService.getAllCourses());
    }

    @GetMapping("/courses/all")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<List<TrainingCourse>> getAllCoursesForAdmin() {
        return ResponseEntity.ok(trainingService.getAllCoursesForAdmin());
    }

    /**
     * POST /api/training/courses
     * Creates a new training course
     */
    @PostMapping("/courses")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        String category = (String) request.get("category");
        Integer durationHours = request.get("durationHours") != null 
            ? Integer.valueOf(request.get("durationHours").toString()) 
            : null;
        String instructor = (String) request.get("instructor");
        String status = (String) request.get("status");
        
        return ResponseEntity.ok(trainingService.createCourse(title, description, category, durationHours, instructor, status));
    }

    /**
     * PUT /api/training/courses/{id}
     * Updates a training course
     */
    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Map<String, Object>> updateCourse(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        String category = (String) request.get("category");
        Integer durationHours = request.get("durationHours") != null 
            ? Integer.valueOf(request.get("durationHours").toString()) 
            : null;
        String instructor = (String) request.get("instructor");
        String status = (String) request.get("status");
        
        return ResponseEntity.ok(trainingService.updateCourse(id, title, description, category, durationHours, instructor, status));
    }

    /**
     * DELETE /api/training/courses/{id}
     * Archives a training course (soft delete)
     */
    @DeleteMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public ResponseEntity<Map<String, Object>> archiveCourse(@PathVariable Long id) {
        return ResponseEntity.ok(trainingService.archiveCourse(id));
    }

    /**
     * GET /api/training/enrollments/{employeeCode}
     * Returns enrollments for an employee
     */
    @GetMapping("/enrollments/{employeeCode}")
    public ResponseEntity<List<Map<String, Object>>> getEnrollments(@PathVariable String employeeCode) {
        return ResponseEntity.ok(trainingService.getEnrollments(employeeCode));
    }

    /**
     * POST /api/training/enroll
     * Enrolls an employee in a course
     */
    @PostMapping("/enroll")
    public ResponseEntity<Map<String, Object>> enrollEmployee(@RequestBody Map<String, Object> request) {
        Long courseId = request.get("courseId") != null 
            ? Long.valueOf(request.get("courseId").toString()) 
            : null;
        String employeeCode = (String) request.get("employeeCode");
        
        if (courseId == null || employeeCode == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "courseId and employeeCode are required"));
        }
        
        return ResponseEntity.ok(trainingService.enrollEmployee(courseId, employeeCode));
    }

    /**
     * PUT /api/training/enrollments/{enrollmentId}/progress
     * Updates progress for an enrollment
     */
    @PutMapping("/enrollments/{enrollmentId}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestBody Map<String, Object> request) {
        Integer progress = request.get("progress") != null 
            ? Integer.valueOf(request.get("progress").toString()) 
            : 0;
        
        return ResponseEntity.ok(trainingService.updateProgress(enrollmentId, progress));
    }
}
