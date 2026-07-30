package com.crm.dto.response;

import java.util.List;

public class BulkEmailResponseDto {

    private int totalRequested;
    private int totalSuccess;
    private int totalFailed;
    private List<EmailResultDto> results;

    public int getTotalRequested() { return totalRequested; }
    public void setTotalRequested(int totalRequested) { this.totalRequested = totalRequested; }

    public int getTotalSuccess() { return totalSuccess; }
    public void setTotalSuccess(int totalSuccess) { this.totalSuccess = totalSuccess; }

    public int getTotalFailed() { return totalFailed; }
    public void setTotalFailed(int totalFailed) { this.totalFailed = totalFailed; }

    public List<EmailResultDto> getResults() { return results; }
    public void setResults(List<EmailResultDto> results) { this.results = results; }
}