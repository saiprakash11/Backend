package com.ems.management.meetings;

import java.time.LocalDateTime;
import java.util.List;

public class MeetingDTO {

    private Long id;
    private String title;
    private String description;
    private String organizer;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String status;
    private List<String> attendees;
    private String meetingLink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MeetingDTO() {}

    public MeetingDTO(Long id, String title, String description, String organizer,
                      LocalDateTime startTime, LocalDateTime endTime, String location,
                      String status, List<String> attendees, String meetingLink,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.title = title; this.description = description;
        this.organizer = organizer; this.startTime = startTime; this.endTime = endTime;
        this.location = location; this.status = status; this.attendees = attendees;
        this.meetingLink = meetingLink; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<String> getAttendees() { return attendees; }
    public void setAttendees(List<String> attendees) { this.attendees = attendees; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
