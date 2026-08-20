package com.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "lead_req_category")
public class LeadRequirementCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requirementCategoryPrimeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_prime_id", nullable = false)
    private LeadEntity lead;

    private String category;

    public LeadRequirementCategoryEntity() {}

    public LeadRequirementCategoryEntity(LeadEntity lead, String category) {
        this.lead = lead;
        this.category = category;
    }

    public Long getRequirementCategoryPrimeId() { return requirementCategoryPrimeId; }
    public void setRequirementCategoryPrimeId(Long id) { this.requirementCategoryPrimeId = id; }

    public LeadEntity getLead() { return lead; }
    public void setLead(LeadEntity lead) { this.lead = lead; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}