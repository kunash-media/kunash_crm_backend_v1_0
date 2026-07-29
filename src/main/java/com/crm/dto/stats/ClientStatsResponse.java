package com.crm.dto.stats;

public class ClientStatsResponse {

    private long totalClients;
    private long totalWebsites;
    private long staticCount;
    private long dynamicCount;

    public ClientStatsResponse(long totalClients, long totalWebsites, long staticCount, long dynamicCount) {
        this.totalClients = totalClients;
        this.totalWebsites = totalWebsites;
        this.staticCount = staticCount;
        this.dynamicCount = dynamicCount;
    }

    public long getTotalClients() { return totalClients; }
    public long getTotalWebsites() { return totalWebsites; }
    public long getStaticCount() { return staticCount; }
    public long getDynamicCount() { return dynamicCount; }
}