package com.crm.dto.stats;


public class LeadStatsResponse {

    private long totalLeads;
    private long todayFollowups;
    private long totalFollowups;
    private long won;
    private long lost;

    public LeadStatsResponse(long totalLeads, long todayFollowups, long totalFollowups, long won, long lost) {
        this.totalLeads = totalLeads;
        this.todayFollowups = todayFollowups;
        this.totalFollowups = totalFollowups;
        this.won = won;
        this.lost = lost;
    }

    public long getTotalLeads() { return totalLeads; }
    public long getTodayFollowups() { return todayFollowups; }
    public long getTotalFollowups() { return totalFollowups; }
    public long getWon() { return won; }
    public long getLost() { return lost; }
}