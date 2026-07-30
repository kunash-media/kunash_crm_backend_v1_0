package com.crm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LeadOutcomeRequest {

    @NotBlank(message = "Outcome is required")
    private String leadOutcome; // "won" or "lost"

    private String lostReason; // required in practice when leadOutcome = "lost"; validated in service

    public String getLeadOutcome() { return leadOutcome; }
    public void setLeadOutcome(String leadOutcome) { this.leadOutcome = leadOutcome; }

    public String getLostReason() { return lostReason; }
    public void setLostReason(String lostReason) { this.lostReason = lostReason; }
}