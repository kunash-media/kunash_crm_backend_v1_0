package com.crm.dto.response;

public class ScheduleMeetingResponseDto {
    private String meetLink;

    public ScheduleMeetingResponseDto() {}
    public ScheduleMeetingResponseDto(String meetLink) { this.meetLink = meetLink; }

    public String getMeetLink() { return meetLink; }
    public void setMeetLink(String meetLink) { this.meetLink = meetLink; }
}