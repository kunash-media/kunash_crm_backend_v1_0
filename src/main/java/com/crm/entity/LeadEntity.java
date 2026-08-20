package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lead_entity")
public class LeadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leadPrimeId;

    @Column(unique = true, nullable = false, updatable = false)
    private String leadStrId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;

    private String status;              // hot / warm / cold
    private String priority;            // P1 / P2 / P3
    private String source;
    private String referralDetails;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeadRequirementCategoryEntity> requirementCategories = new ArrayList<>();

    private String tags;

    private LocalDate followUpDate;
    private String followupStatus;      // pending / done / rescheduled etc.

    @Column(length = 2000)
    private String notes;

    private Boolean deletedLead = false;
    private Boolean leadConverted = false;

    private String leadOutcome; // null = open, "won", "lost" — independent of deletedLead
    private String lostReason; // only meaningful when leadOutcome = "lost"

    @Lob
    @Column(name = "doc_file", columnDefinition = "LONGBLOB")
    private byte[] docFile;

    private String docFileName;
    private String docFileType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "lead", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LeadFollowupEntity> followups = new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_staff_id")
    private StaffEntity assignedStaff;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.leadStrId == null) {
            String stamp = this.createdAt.format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-HHmmss-SSS"));
            this.leadStrId = "LEAD-" + stamp;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    public void applyRequirementCategoriesFromRaw(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) return;
        this.requirementCategories.clear();
        for (String part : rawValue.split("[,;]")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                this.requirementCategories.add(new LeadRequirementCategoryEntity(this, trimmed));
            }
        }
    }

    // getters and setters

    public Long getLeadPrimeId() { return leadPrimeId; }
    public void setLeadPrimeId(Long leadPrimeId) { this.leadPrimeId = leadPrimeId; }

    public String getLeadStrId() { return leadStrId; }
    public void setLeadStrId(String leadStrId) { this.leadStrId = leadStrId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public List<LeadRequirementCategoryEntity> getRequirementCategories() {
        return requirementCategories;
    }

    public void setRequirementCategories(List<LeadRequirementCategoryEntity> requirementCategories) {
        this.requirementCategories = requirementCategories;
    }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }

    public String getFollowupStatus() { return followupStatus; }
    public void setFollowupStatus(String followupStatus) { this.followupStatus = followupStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getDeletedLead() { return deletedLead; }
    public void setDeletedLead(Boolean deletedLead) { this.deletedLead = deletedLead; }

    public Boolean getLeadConverted() { return leadConverted; }
    public void setLeadConverted(Boolean leadConverted) { this.leadConverted = leadConverted; }

    public byte[] getDocFile() { return docFile; }
    public void setDocFile(byte[] docFile) { this.docFile = docFile; }

    public String getDocFileName() { return docFileName; }
    public void setDocFileName(String docFileName) { this.docFileName = docFileName; }

    public String getDocFileType() { return docFileType; }
    public void setDocFileType(String docFileType) { this.docFileType = docFileType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public java.util.List<LeadFollowupEntity> getFollowups() { return followups; }
    public void setFollowups(java.util.List<LeadFollowupEntity> followups) { this.followups = followups; }

    public String getLeadOutcome() { return leadOutcome; }
    public void setLeadOutcome(String leadOutcome) { this.leadOutcome = leadOutcome; }

    public String getLostReason() {
        return lostReason;
    }

    public void setLostReason(String lostReason) {
        this.lostReason = lostReason;
    }

    public com.crm.entity.StaffEntity getAssignedStaff() {
        return assignedStaff;
    }

    public void setAssignedStaff(com.crm.entity.StaffEntity assignedStaff) {
        this.assignedStaff = assignedStaff;
    }

    public String getReferralDetails() {
        return referralDetails;
    }

    public void setReferralDetails(String referralDetails) {
        this.referralDetails = referralDetails;
    }
}