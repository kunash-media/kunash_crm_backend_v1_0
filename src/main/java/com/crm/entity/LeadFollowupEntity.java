package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_followup_entity")
public class LeadFollowupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long followupPrimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_prime_id", nullable = false)
    private LeadEntity lead;

    private LocalDate followupDate;
    private String followupStatus;

    @Column(length = 1000)
    private String followupNotes;

    private Boolean deletedFollowup = false;

    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
    }

    public Long getFollowupPrimeId() { return followupPrimeId; }
    public void setFollowupPrimeId(Long followupPrimeId) { this.followupPrimeId = followupPrimeId; }

    public LeadEntity getLead() { return lead; }
    public void setLead(LeadEntity lead) { this.lead = lead; }

    public LocalDate getFollowupDate() { return followupDate; }
    public void setFollowupDate(LocalDate followupDate) { this.followupDate = followupDate; }

    public String getFollowupStatus() { return followupStatus; }
    public void setFollowupStatus(String followupStatus) { this.followupStatus = followupStatus; }

    public String getFollowupNotes() { return followupNotes; }
    public void setFollowupNotes(String followupNotes) { this.followupNotes = followupNotes; }

    public Boolean getDeletedFollowup() { return deletedFollowup; }
    public void setDeletedFollowup(Boolean deletedFollowup) { this.deletedFollowup = deletedFollowup; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}