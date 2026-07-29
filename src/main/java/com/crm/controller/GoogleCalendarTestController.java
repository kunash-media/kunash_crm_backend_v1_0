package com.crm.controller;

import com.crm.util.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/oauth2/test")
public class GoogleCalendarTestController {

    private final GoogleCalendarService calendarService;

    @Autowired
    public GoogleCalendarTestController(GoogleCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    // quick manual test — creates a meeting 10 minutes from now, returns the Meet link
    @GetMapping("/create-meeting")
    public ResponseEntity<String> testCreateMeeting() {
        String meetLink = calendarService.createMeetingAndGetMeetLink(
                "Test Meeting - Kunash CRM",
                "Verifying Meet link generation works end-to-end.",
                LocalDateTime.now().plusMinutes(10),
                30
        );
        return ResponseEntity.ok(meetLink);
    }
}