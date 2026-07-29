package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "client_list_entity")
public class ClientListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientPrimeId;

    @Column(unique = true, nullable = false, updatable = false)
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

    // Lineage: which lead (if any) this client was converted from.
    // Plain nullable FK-by-id (not a JPA relation) — keeps this entity
    // decoupled from LeadEntity while still letting us trace provenance
    // and guard against double-conversion.
    private Long sourceLeadId;

    private Boolean deletedClient = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String assignTo;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.clientStrId == null) {
            this.clientStrId = "CLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.deletedClient == null) {
            this.deletedClient = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ──
    public Long getClientPrimeId() { return clientPrimeId; }
    public void setClientPrimeId(Long clientPrimeId) { this.clientPrimeId = clientPrimeId; }

    public String getClientStrId() { return clientStrId; }
    public void setClientStrId(String clientStrId) { this.clientStrId = clientStrId; }

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

    public Long getSourceLeadId() { return sourceLeadId; }
    public void setSourceLeadId(Long sourceLeadId) { this.sourceLeadId = sourceLeadId; }

    public Boolean getDeletedClient() { return deletedClient; }
    public void setDeletedClient(Boolean deletedClient) { this.deletedClient = deletedClient; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getAssignTo() { return assignTo; }
    public void setAssignTo(String assignTo) { this.assignTo = assignTo; }
}