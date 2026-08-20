package com.crm.dto.request;

import java.time.LocalDate;

public class LeadFollowupRequestDto {
    private LocalDate followupDate;
    private String followupStatus;
    private String followupNotes;

    private Long takenByStaffId;
    private String meetingType;


    public LocalDate getFollowupDate() { return followupDate; }
    public void setFollowupDate(LocalDate followupDate) { this.followupDate = followupDate; }

    public String getFollowupStatus() { return followupStatus; }
    public void setFollowupStatus(String followupStatus) { this.followupStatus = followupStatus; }

    public String getFollowupNotes() { return followupNotes; }
    public void setFollowupNotes(String followupNotes) { this.followupNotes = followupNotes; }

    public Long getTakenByStaffId() {
        return takenByStaffId;
    }

    public void setTakenByStaffId(Long takenByStaffId) {
        this.takenByStaffId = takenByStaffId;
    }

    public String getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(String meetingType) {
        this.meetingType = meetingType;
    }
}