package com.crm.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ClientCreateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Contact number is required")
    private String contact;

    private String email;

    @NotBlank(message = "Service is required")
    private String service;

    private String project;
    private String source;
    private String type;

    private Double totalAmount;
    private Double advanceAmount;
    private Double remainAmount;
    private Double pendingAmount;

    private String assignTo;

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getProject() { return project; }
    public void setProject(String project) { this.project = project; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getAdvanceAmount() { return advanceAmount; }
    public void setAdvanceAmount(Double advanceAmount) { this.advanceAmount = advanceAmount; }

    public Double getRemainAmount() { return remainAmount; }
    public void setRemainAmount(Double remainAmount) { this.remainAmount = remainAmount; }

    public Double getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(Double pendingAmount) { this.pendingAmount = pendingAmount; }

    public String getAssignTo() { return assignTo; }
    public void setAssignTo(String assignTo) { this.assignTo = assignTo; }
}