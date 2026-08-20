package com.crm.dto.stats;


public class StaffStatsDto {

    private long totalStaff;
    private long totalLeads;
    private long pendingFollowups;

    public StaffStatsDto() {
    }

    public StaffStatsDto(long totalStaff, long totalLeads, long pendingFollowups) {
        this.totalStaff = totalStaff;
        this.totalLeads = totalLeads;
        this.pendingFollowups = pendingFollowups;
    }

    public long getTotalStaff() {
        return totalStaff;
    }

    public void setTotalStaff(long totalStaff) {
        this.totalStaff = totalStaff;
    }

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getPendingFollowups() {
        return pendingFollowups;
    }

    public void setPendingFollowups(long pendingFollowups) {
        this.pendingFollowups = pendingFollowups;
    }
}