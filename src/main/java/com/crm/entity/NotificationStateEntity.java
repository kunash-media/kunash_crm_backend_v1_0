package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_state", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"adminId", "fingerprint"})
})
public class NotificationStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminId;

    // Unique id per alert item, e.g. "pending-CLT-A1B2C3D4"
    private String fingerprint;

    private LocalDateTime visitedAt;

    public NotificationStateEntity() {}

    public NotificationStateEntity(String adminId, String fingerprint) {
        this.adminId = adminId;
        this.fingerprint = fingerprint;
        this.visitedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
}
