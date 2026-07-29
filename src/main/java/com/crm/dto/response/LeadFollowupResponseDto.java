package com.crm.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeadFollowupResponseDto {
    private Long followupPrimeId;
    private LocalDate followupDate;
    private String followupStatus;
    private String followupNotes;
    private LocalDateTime createdAt;

    public Long getFollowupPrimeId() { return followupPrimeId; }
    public void setFollowupPrimeId(Long followupPrimeId) { this.followupPrimeId = followupPrimeId; }

    public LocalDate getFollowupDate() { return followupDate; }
    public void setFollowupDate(LocalDate followupDate) { this.followupDate = followupDate; }

    public String getFollowupStatus() { return followupStatus; }
    public void setFollowupStatus(String followupStatus) { this.followupStatus = followupStatus; }

    public String getFollowupNotes() { return followupNotes; }
    public void setFollowupNotes(String followupNotes) { this.followupNotes = followupNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}