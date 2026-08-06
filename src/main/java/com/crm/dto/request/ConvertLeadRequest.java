package com.crm.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ConvertLeadRequest {

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    @NotNull(message = "Advance amount is required")
    private Double advanceAmount;

    // Optional — defaults to 0 server-side if not provided
    private Double pendingAmount;

    private LocalDate remainPayFollowUpDate;

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(Double advanceAmount) { this.advanceAmount = advanceAmount; }

    public Double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(Double pendingAmount) { this.pendingAmount = pendingAmount; }

    public LocalDate getRemainPayFollowUpDate() {
        return remainPayFollowUpDate;
    }

    public void setRemainPayFollowUpDate(LocalDate remainPayFollowUpDate) {
        this.remainPayFollowUpDate = remainPayFollowUpDate;
    }
}