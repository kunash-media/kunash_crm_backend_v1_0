package com.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reason_bucket_entity")
public class ReasonBucketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bucketPrimeId;

    @Column(nullable = false, unique = true)
    private String bucketName;          // "Pricing", "Timing", "Competitor"...

    private String applicableTo;        // "LOST" / "WON" / "BOTH"

    @Column(length = 1000)
    private String keywords;            // comma-separated: "price,expensive,budget,cost"

    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getBucketPrimeId() { return bucketPrimeId; }
    public void setBucketPrimeId(Long bucketPrimeId) { this.bucketPrimeId = bucketPrimeId; }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getApplicableTo() { return applicableTo; }
    public void setApplicableTo(String applicableTo) { this.applicableTo = applicableTo; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}