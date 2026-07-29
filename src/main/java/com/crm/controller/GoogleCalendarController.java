package com.crm.controller;

import com.crm.dto.request.ScheduleMeetingRequestDto;
import com.crm.dto.response.ScheduleMeetingResponseDto;
import com.crm.util.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/calendar/v1")
public class GoogleCalendarController {

    private final GoogleCalendarService calendarService;

    @Autowired
    public GoogleCalendarController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/schedule-meeting")
    public ResponseEntity<ScheduleMeetingResponseDto> scheduleMeeting(@RequestBody ScheduleMeetingRequestDto dto) {
        LocalDate date = LocalDate.parse(dto.getMeetingDate());
        LocalTime time = LocalTime.parse(dto.getMeetingTime());
        LocalDateTime startDateTime = LocalDateTime.of(date, time);

        int duration = dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 30;

        String summary = "Meeting with " + dto.getLeadFirstName() + " " + dto.getLeadLastName();
        String description = "Meeting regarding " + (dto.getRequirementCategory() != null ? dto.getRequirementCategory() : "your requirement")
                + " — scheduled via Kunash CRM.";

        String meetLink = calendarService.createMeetingAndGetMeetLink(summary, description, startDateTime, duration);

        return ResponseEntity.ok(new ScheduleMeetingResponseDto(meetLink));
    }
}