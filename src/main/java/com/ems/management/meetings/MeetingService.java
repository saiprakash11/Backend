package com.ems.management.meetings;

import com.ems.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;

    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    public Meeting createMeeting(MeetingDTO dto) {
        Meeting meeting = new Meeting();
        meeting.setTitle(dto.getTitle());
        meeting.setDescription(dto.getDescription());
        meeting.setOrganizer(dto.getOrganizer());
        meeting.setStartTime(dto.getStartTime());
        meeting.setEndTime(dto.getEndTime());
        meeting.setLocation(dto.getLocation());
        meeting.setMeetingLink(dto.getMeetingLink());
        meeting.setAttendees(dto.getAttendees());
        meeting.setStatus("Scheduled");
        // createdAt and updatedAt set by @PrePersist
        return meetingRepository.save(meeting);
    }

    public Meeting getMeetingById(Long id) {
        return meetingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Meeting not found with id: " + id));
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public List<Meeting> getMeetingsByStatus(String status) {
        return meetingRepository.findByStatus(status);
    }

    public List<Meeting> getScheduledMeetings() {
        return meetingRepository.findByStatusOrderByStartTimeAsc("Scheduled");
    }

    public List<Meeting> getMeetingsByOrganizer(String organizer) {
        return meetingRepository.findByOrganizer(organizer);
    }

    public List<Meeting> getMeetingsByAttendee(String attendee) {
        return meetingRepository.findMeetingsByAttendee(attendee);
    }

    public List<Meeting> getMeetingsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return meetingRepository.findMeetingsBetweenDates(startDate, endDate);
    }

    public Meeting updateMeeting(Long id, MeetingDTO dto) {
        Meeting meeting = getMeetingById(id);

        if (dto.getTitle() != null) {
            meeting.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            meeting.setDescription(dto.getDescription());
        }
        if (dto.getStartTime() != null) {
            meeting.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            meeting.setEndTime(dto.getEndTime());
        }
        if (dto.getLocation() != null) {
            meeting.setLocation(dto.getLocation());
        }
        if (dto.getMeetingLink() != null) {
            meeting.setMeetingLink(dto.getMeetingLink());
        }
        if (dto.getAttendees() != null && !dto.getAttendees().isEmpty()) {
            meeting.setAttendees(dto.getAttendees());
        }

        // updatedAt handled by @PreUpdate
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeetingStatus(Long id, String status) {
        Meeting meeting = getMeetingById(id);
        meeting.setStatus(status);
        // updatedAt handled by @PreUpdate
        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id) {
        Meeting meeting = getMeetingById(id);
        meetingRepository.delete(meeting);
    }

    public void addAttendee(Long id, String attendee) {
        Meeting meeting = getMeetingById(id);
        if (!meeting.getAttendees().contains(attendee)) {
            meeting.getAttendees().add(attendee);
            meetingRepository.save(meeting);
        }
    }

    public void removeAttendee(Long id, String attendee) {
        Meeting meeting = getMeetingById(id);
        meeting.getAttendees().remove(attendee);
        meetingRepository.save(meeting);
    }
}
