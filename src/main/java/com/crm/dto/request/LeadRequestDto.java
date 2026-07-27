package com.crm.dto.request;

import java.time.LocalDate;

public class LeadRequestDto {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String status;
    private String priority;
    private String source;
    private String requirementCategory;
    private String tags;
    private LocalDate followUpDate;
    private String followupStatus;
    private String notes;
    private Boolean leadConverted;

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

    public String getRequirementCategory() { return requirementCategory; }
    public void setRequirementCategory(String requirementCategory) { this.requirementCategory = requirementCategory; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDate getFollowUpDate() { return followUpDate; }
    public void setFollowUpDate(LocalDate followUpDate) { this.followUpDate = followUpDate; }

    public String getFollowupStatus() { return followupStatus; }
    public void setFollowupStatus(String followupStatus) { this.followupStatus = followupStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getLeadConverted() { return leadConverted; }
    public void setLeadConverted(Boolean leadConverted) { this.leadConverted = leadConverted; }
}