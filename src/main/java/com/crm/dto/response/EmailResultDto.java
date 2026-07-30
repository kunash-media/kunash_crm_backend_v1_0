package com.crm.dto.response;

public class EmailResultDto {

    private Long leadPrimeId;
    private String leadStrId;
    private String email;
    private boolean success;
    private String message;   // "Email sent successfully" OR the failure reason

    public static EmailResultDto success(Long leadPrimeId, String leadStrId, String email) {
        EmailResultDto dto = new EmailResultDto();
        dto.leadPrimeId = leadPrimeId;
        dto.leadStrId = leadStrId;
        dto.email = email;
        dto.success = true;
        dto.message = "Email sent successfully";
        return dto;
    }

    public static EmailResultDto failure(Long leadPrimeId, String leadStrId, String email, String reason) {
        EmailResultDto dto = new EmailResultDto();
        dto.leadPrimeId = leadPrimeId;
        dto.leadStrId = leadStrId;
        dto.email = email;
        dto.success = false;
        dto.message = reason;
        return dto;
    }

    public Long getLeadPrimeId() { return leadPrimeId; }
    public void setLeadPrimeId(Long leadPrimeId) { this.leadPrimeId = leadPrimeId; }

    public String getLeadStrId() { return leadStrId; }
    public void setLeadStrId(String leadStrId) { this.leadStrId = leadStrId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}