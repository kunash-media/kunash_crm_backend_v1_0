package com.crm.dto.stats;

public class MonthlyLeadCountDto {
    private String monthKey;   // e.g. "2026-07"
    private String monthLabel; // e.g. "Jul"
    private int year;
    private long count;

    public MonthlyLeadCountDto() {}

    public MonthlyLeadCountDto(String monthKey, String monthLabel, int year, long count) {
        this.monthKey = monthKey;
        this.monthLabel = monthLabel;
        this.year = year;
        this.count = count;
    }

    public String getMonthKey() { return monthKey; }
    public void setMonthKey(String monthKey) { this.monthKey = monthKey; }

    public String getMonthLabel() { return monthLabel; }
    public void setMonthLabel(String monthLabel) { this.monthLabel = monthLabel; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}