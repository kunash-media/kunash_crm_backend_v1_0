package com.crm.dto.request;

public class ScheduleMeetingRequestDto {
    private String leadFirstName;
    private String leadLastName;
    private String requirementCategory;
    private String meetingDate;   // format: yyyy-MM-dd
    private String meetingTime;   // format: HH:mm
    private Integer durationMinutes; // optional, defaults to 30

    public String getLeadFirstName() { return leadFirstName; }
    public void setLeadFirstName(String leadFirstName) { this.leadFirstName = leadFirstName; }

    public String getLeadLastName() { return leadLastName; }
    public void setLeadLastName(String leadLastName) { this.leadLastName = leadLastName; }

    public String getRequirementCategory() { return requirementCategory; }
    public void setRequirementCategory(String requirementCategory) { this.requirementCategory = requirementCategory; }

    public String getMeetingDate() { return meetingDate; }
    public void setMeetingDate(String meetingDate) { this.meetingDate = meetingDate; }

    public String getMeetingTime() { return meetingTime; }
    public void setMeetingTime(String meetingTime) { this.meetingTime = meetingTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
}