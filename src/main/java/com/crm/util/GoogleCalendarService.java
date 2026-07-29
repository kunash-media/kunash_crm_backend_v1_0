package com.crm.util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ConferenceData;
import com.google.api.services.calendar.model.ConferenceSolutionKey;
import com.google.api.services.calendar.model.CreateConferenceRequest;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Kunash CRM";
    private static final String TIME_ZONE = "Asia/Kolkata";

    private final GoogleAuthService googleAuthService;

    @Autowired
    public GoogleCalendarService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    private Calendar buildCalendarClient() {
        try {
            String accessToken = googleAuthService.getValidAccessToken();
            com.google.api.client.auth.oauth2.Credential credential =
                    new com.google.api.client.auth.oauth2.Credential(
                            com.google.api.client.auth.oauth2.BearerToken.authorizationHeaderAccessMethod())
                            .setAccessToken(accessToken);

            return new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Google Calendar client", e);
        }
    }

    /**
     * Creates a Calendar event with Google Meet conferencing attached, and
     * returns the real meet.google.com join link.
     */
    public String createMeetingAndGetMeetLink(String summary, String description, LocalDateTime startDateTime, int durationMinutes) {
        try {
            Calendar service = buildCalendarClient();

            Event event = new Event()
                    .setSummary(summary)
                    .setDescription(description);

            ZoneId zone = ZoneId.of(TIME_ZONE);
            DateTime start = new DateTime(startDateTime.atZone(zone).toInstant().toEpochMilli());
            DateTime end = new DateTime(startDateTime.plusMinutes(durationMinutes).atZone(zone).toInstant().toEpochMilli());

            event.setStart(new EventDateTime().setDateTime(start).setTimeZone(TIME_ZONE));
            event.setEnd(new EventDateTime().setDateTime(end).setTimeZone(TIME_ZONE));

            // request Google Meet conferencing
            ConferenceSolutionKey key = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest createRequest = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(key);
            ConferenceData conferenceData = new ConferenceData().setCreateRequest(createRequest);
            event.setConferenceData(conferenceData);

            Event created = service.events()
                    .insert("primary", event)
                    .setConferenceDataVersion(1) // required for conferenceData to be honored
                    .execute();

            if (created.getConferenceData() != null
                    && created.getConferenceData().getEntryPoints() != null) {
                return created.getConferenceData().getEntryPoints().stream()
                        .filter(ep -> "video".equals(ep.getEntryPointType()))
                        .map(ep -> ep.getUri())
                        .findFirst()
                        .orElse(created.getHtmlLink()); // fallback: link to the calendar event itself
            }

            return created.getHtmlLink();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Calendar event with Meet link", e);
        }
    }
}