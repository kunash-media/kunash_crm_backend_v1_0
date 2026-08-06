package com.crm.dto.response;

import com.crm.entity.ClientListEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientCreatedResponse {

    private Long clientPrimeId;
    private String clientStrId;
    private String firstName;
    private String lastName;
    private String contact;
    private String email;
    private String service;
    private String project;
    private String source;
    private String type;
    private Double totalAmount;
    private Double advanceAmount;
    private Double remainAmount;
    private Double pendingAmount;
    private Long sourceLeadId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String assignTo;

    private LocalDate remainPayFollowUpDate;

    public static ClientCreatedResponse from(ClientListEntity entity) {
        ClientCreatedResponse res = new ClientCreatedResponse();
        res.clientPrimeId = entity.getClientPrimeId();
        res.clientStrId = entity.getClientStrId();
        res.firstName = entity.getFirstName();
        res.lastName = entity.getLastName();
        res.contact = entity.getContact();
        res.email = entity.getEmail();
        res.service = entity.getService();
        res.project = entity.getProject();
        res.source = entity.getSource();
        res.type = entity.getType();
        res.totalAmount = entity.getTotalAmount();
        res.advanceAmount = entity.getAdvanceAmount();
        res.remainAmount = entity.getRemainAmount();
        res.pendingAmount = entity.getPendingAmount();
        res.sourceLeadId = entity.getSourceLeadId();
        res.createdAt = entity.getCreatedAt();
        res.updatedAt = entity.getUpdatedAt();
        res.assignTo = entity.getAssignTo();
        res.remainPayFollowUpDate =  entity.getRemainPayFollowUpDate();

        return res;
    }

    // ── Getters (response-only, no setters needed) ──
    public Long getClientPrimeId() { return clientPrimeId; }
    public String getClientStrId() { return clientStrId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getService() { return service; }
    public String getProject() { return project; }
    public String getSource() { return source; }
    public String getType() { return type; }
    public Double getTotalAmount() { return totalAmount; }
    public Double getAdvanceAmount() { return advanceAmount; }
    public Double getRemainAmount() { return remainAmount; }
    public Double getPendingAmount() { return pendingAmount; }
    public Long getSourceLeadId() { return sourceLeadId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getAssignTo() { return assignTo; }

    public LocalDate getRemainPayFollowUpDate() {
        return remainPayFollowUpDate;
    }

    public void setRemainPayFollowUpDate(LocalDate remainPayFollowUpDate) {
        this.remainPayFollowUpDate = remainPayFollowUpDate;
    }
}