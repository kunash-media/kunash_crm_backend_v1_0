package com.crm.dto.stats;

import com.crm.entity.ClientListEntity;

import java.time.LocalDateTime;

public class PendingPaymentAlertResponse {

    private Long clientPrimeId;
    private String clientStrId;
    private String clientName;
    private String contact;
    private Double totalAmount;
    private Double advanceAmount;
    private Double pendingAmount;
    private String severity;      // LOW / MEDIUM / HIGH
    private LocalDateTime createdAt;

    public static PendingPaymentAlertResponse from(ClientListEntity entity) {
        PendingPaymentAlertResponse dto = new PendingPaymentAlertResponse();
        dto.clientPrimeId = entity.getClientPrimeId();
        dto.clientStrId = entity.getClientStrId();
        dto.clientName = buildName(entity.getFirstName(), entity.getLastName());
        dto.contact = entity.getContact();
        dto.totalAmount = entity.getTotalAmount();
        dto.advanceAmount = entity.getAdvanceAmount();
        dto.pendingAmount = entity.getPendingAmount();
        dto.severity = computeSeverity(entity.getPendingAmount());
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }

    private static String buildName(String first, String last) {
        if (last == null || last.isBlank()) return first;
        return first + " " + last;
    }

    private static String computeSeverity(Double pendingAmount) {
        if (pendingAmount == null) return "LOW";
        if (pendingAmount >= 50000) return "HIGH";
        if (pendingAmount >= 10000) return "MEDIUM";
        return "LOW";
    }

    // ── Getters & Setters ──
    public Long getClientPrimeId() { return clientPrimeId; }
    public void setClientPrimeId(Long clientPrimeId) { this.clientPrimeId = clientPrimeId; }

    public String getClientStrId() { return clientStrId; }
    public void setClientStrId(String clientStrId) { this.clientStrId = clientStrId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(Double advanceAmount) { this.advanceAmount = advanceAmount; }

    public Double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(Double pendingAmount) { this.pendingAmount = pendingAmount; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}