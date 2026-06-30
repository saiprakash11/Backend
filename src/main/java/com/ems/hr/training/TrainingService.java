package com.ems.hr.training;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class TrainingService {

    private final TrainingCourseRepository courseRepository;
    private final TrainingEnrollmentRepository enrollmentRepository;

    public TrainingService(TrainingCourseRepository courseRepository,
                           TrainingEnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<TrainingCourse> getAllCourses() {
        return courseRepository.findByStatusOrderByCreatedAtDesc("Active");
    }

    public List<TrainingCourse> getAllCoursesForAdmin() {
        return courseRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Map<String, Object> createCourse(String title, String description, String category,
                                             Integer durationHours, String instructor, String status) {
        TrainingCourse course = new TrainingCourse();
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        course.setDurationHours(durationHours);
        course.setInstructor(instructor);
        course.setStatus(status != null ? status : "Active");
        courseRepository.save(course);
        return Map.of("success", true, "message", "Course created successfully");
    }

    @Transactional
    public Map<String, Object> updateCourse(Long id, String title, String description, String category,
                                             Integer durationHours, String instructor, String status) {
        var opt = courseRepository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("success", false, "message", "Course not found");
        }
        TrainingCourse course = opt.get();
        if (title != null) course.setTitle(title);
        if (description != null) course.setDescription(description);
        if (category != null) course.setCategory(category);
        if (durationHours != null) course.setDurationHours(durationHours);
        if (instructor != null) course.setInstructor(instructor);
        if (status != null) course.setStatus(status);
        courseRepository.save(course);
        return Map.of("success", true, "message", "Course updated successfully");
    }

    @Transactional
    public Map<String, Object> archiveCourse(Long id) {
        var opt = courseRepository.findById(id);
        if (opt.isEmpty()) {
            return Map.of("success", false, "message", "Course not found");
        }
        TrainingCourse course = opt.get();
        course.setStatus("Archived");
        courseRepository.save(course);
        return Map.of("success", true, "message", "Course archived successfully");
    }

    public List<Map<String, Object>> getEnrollments(String employeeCode) {
        List<TrainingEnrollment> enrollments = enrollmentRepository.findByEmployeeCodeOrderByEnrolledAtDesc(employeeCode);
        List<Map<String, Object>> result = new ArrayList<>();
        for (TrainingEnrollment e : enrollments) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("course_id", e.getCourseId());
            map.put("employee_code", e.getEmployeeCode());
            map.put("status", e.getStatus());
            map.put("progress", e.getProgress());
            map.put("enrolled_at", e.getEnrolledAt());
            map.put("completed_at", e.getCompletedAt());
            var course = courseRepository.findById(e.getCourseId());
            course.ifPresent(c -> {
                map.put("title", c.getTitle());
                map.put("category", c.getCategory());
                map.put("duration_hours", c.getDurationHours());
            });
            result.add(map);
        }
        return result;
    }

    @Transactional
    public Map<String, Object> enrollEmployee(Long courseId, String employeeCode) {
        var existing = enrollmentRepository.findByCourseIdAndEmployeeCode(courseId, employeeCode);
        if (existing.isPresent()) {
            return Map.of("success", false, "message", "Already enrolled in this course");
        }
        if (courseRepository.findById(courseId).isEmpty()) {
            return Map.of("success", false, "message", "Course not found");
        }
        TrainingEnrollment enrollment = new TrainingEnrollment();
        enrollment.setCourseId(courseId);
        enrollment.setEmployeeCode(employeeCode);
        enrollment.setStatus("Enrolled");
        enrollment.setProgress(0);
        enrollmentRepository.save(enrollment);
        return Map.of("success", true, "message", "Enrolled successfully");
    }

    @Transactional
    public Map<String, Object> updateProgress(Long enrollmentId, Integer progress) {
        var opt = enrollmentRepository.findById(enrollmentId);
        if (opt.isEmpty()) {
            return Map.of("success", false, "message", "Enrollment not found");
        }
        TrainingEnrollment enrollment = opt.get();
        enrollment.setProgress(progress);
        String status = progress >= 100 ? "Completed" : (progress > 0 ? "In Progress" : "Enrolled");
        enrollment.setStatus(status);
        if (progress >= 100 && enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(java.time.LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);
        return Map.of("success", true, "message", "Progress updated successfully");
    }
}
