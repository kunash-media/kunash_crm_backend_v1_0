package com.crm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class AssignClientRequest {

    @NotBlank(message = "Assignee name is required")
    private String assignTo;

    public String getAssignTo() { return assignTo; }
    public void setAssignTo(String assignTo) { this.assignTo = assignTo; }
}