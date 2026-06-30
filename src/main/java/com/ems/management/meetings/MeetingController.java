package com.ems.management.meetings;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    
    private final MeetingService meetingService;
    
    public MeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }
    
    /**
     * GET /api/meetings
     * Returns all meetings
     */
    @GetMapping
    public ResponseEntity<List<Meeting>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }
    
    /**
     * GET /api/meetings/{id}
     * Returns meeting by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Meeting> getMeetingById(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeetingById(id));
    }
    
    /**
     * POST /api/meetings
     * Creates a new meeting
     */
    @PostMapping
    public ResponseEntity<Meeting> createMeeting(@RequestBody MeetingDTO meetingDTO) {
        Meeting meeting = meetingService.createMeeting(meetingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(meeting);
    }
    
    /**
     * GET /api/meetings/status/{status}
     * Returns meetings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Meeting>> getMeetingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(meetingService.getMeetingsByStatus(status));
    }
    
    /**
     * GET /api/meetings/scheduled
     * Returns all scheduled meetings
     */
    @GetMapping("/scheduled")
    public ResponseEntity<List<Meeting>> getScheduledMeetings() {
        return ResponseEntity.ok(meetingService.getScheduledMeetings());
    }
    
    /**
     * GET /api/meetings/organizer/{organizer}
     * Returns meetings organized by a user
     */
    @GetMapping("/organizer/{organizer}")
    public ResponseEntity<List<Meeting>> getMeetingsByOrganizer(@PathVariable String organizer) {
        return ResponseEntity.ok(meetingService.getMeetingsByOrganizer(organizer));
    }
    
    /**
     * GET /api/meetings/attendee/{attendee}
     * Returns meetings attended by a user
     */
    @GetMapping("/attendee/{attendee}")
    public ResponseEntity<List<Meeting>> getMeetingsByAttendee(@PathVariable String attendee) {
        return ResponseEntity.ok(meetingService.getMeetingsByAttendee(attendee));
    }
    
    /**
     * GET /api/meetings/between?startDate=yyyy-MM-ddThh:mm:ss&endDate=yyyy-MM-ddThh:mm:ss
     * Returns meetings between two dates
     */
    @GetMapping("/between")
    public ResponseEntity<List<Meeting>> getMeetingsBetweenDates(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(meetingService.getMeetingsBetweenDates(startDate, endDate));
    }
    
    /**
     * PUT /api/meetings/{id}
     * Updates a meeting
     */
    @PutMapping("/{id}")
    public ResponseEntity<Meeting> updateMeeting(
            @PathVariable Long id,
            @RequestBody MeetingDTO meetingDTO) {
        
        Meeting meeting = meetingService.updateMeeting(id, meetingDTO);
        return ResponseEntity.ok(meeting);
    }
    
    /**
     * PUT /api/meetings/{id}/status
     * Updates meeting status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Meeting> updateMeetingStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String status = request.get("status");
        Meeting meeting = meetingService.updateMeetingStatus(id, status);
        return ResponseEntity.ok(meeting);
    }
    
    /**
     * POST /api/meetings/{id}/attendees
     * Adds an attendee to a meeting
     */
    @PostMapping("/{id}/attendees")
    public ResponseEntity<Map<String, String>> addAttendee(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String attendee = request.get("attendee");
        meetingService.addAttendee(id, attendee);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendee added successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/meetings/{id}/attendees/{attendee}
     * Removes an attendee from a meeting
     */
    @DeleteMapping("/{id}/attendees/{attendee}")
    public ResponseEntity<Map<String, String>> removeAttendee(
            @PathVariable Long id,
            @PathVariable String attendee) {
        
        meetingService.removeAttendee(id, attendee);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Attendee removed successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * DELETE /api/meetings/{id}
     * Deletes a meeting
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMeeting(@PathVariable Long id) {
        meetingService.deleteMeeting(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Meeting deleted successfully");
        return ResponseEntity.ok(response);
    }
}
