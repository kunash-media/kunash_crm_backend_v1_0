package com.crm.dto.stats;

import java.util.List;


public class ReasonReportResponseDto {
    private long totalRecords;
    private List<BucketCountDto> bucketBreakdown;
    private List<TrendPointDto> trend;

    public ReasonReportResponseDto() {}
    public ReasonReportResponseDto(long totalRecords, List<BucketCountDto> bucketBreakdown, List<TrendPointDto> trend) {
        this.totalRecords = totalRecords;
        this.bucketBreakdown = bucketBreakdown;
        this.trend = trend;
    }

    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
    public List<BucketCountDto> getBucketBreakdown() { return bucketBreakdown; }
    public void setBucketBreakdown(List<BucketCountDto> bucketBreakdown) { this.bucketBreakdown = bucketBreakdown; }
    public List<TrendPointDto> getTrend() { return trend; }
    public void setTrend(List<TrendPointDto> trend) { this.trend = trend; }
}