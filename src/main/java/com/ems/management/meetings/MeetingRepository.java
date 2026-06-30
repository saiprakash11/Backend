package com.ems.management.meetings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    
    List<Meeting> findByStatus(String status);
    
    List<Meeting> findByOrganizer(String organizer);
    
    @Query("SELECT m FROM Meeting m WHERE m.startTime >= :startDate AND m.startTime <= :endDate")
    List<Meeting> findMeetingsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT m FROM Meeting m WHERE :attendee MEMBER OF m.attendees")
    List<Meeting> findMeetingsByAttendee(@Param("attendee") String attendee);
    
    List<Meeting> findByStatusOrderByStartTimeAsc(String status);
}
