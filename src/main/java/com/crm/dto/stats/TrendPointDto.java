package com.crm.dto.stats;

import java.util.Map;

public class TrendPointDto {
    private String periodLabel;      // "2026-W32", "2026-08", "2026-Q3", "2026-H1", "2026"
    private Map<String, Long> bucketCounts;

    public TrendPointDto() {}
    public TrendPointDto(String periodLabel, Map<String, Long> bucketCounts) {
        this.periodLabel = periodLabel;
        this.bucketCounts = bucketCounts;
    }

    public String getPeriodLabel() { return periodLabel; }
    public void setPeriodLabel(String periodLabel) { this.periodLabel = periodLabel; }
    public Map<String, Long> getBucketCounts() { return bucketCounts; }
    public void setBucketCounts(Map<String, Long> bucketCounts) { this.bucketCounts = bucketCounts; }
}