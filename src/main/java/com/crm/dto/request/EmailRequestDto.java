package com.crm.dto.request;

import java.util.List;

public class EmailRequestDto {

    // for bulk send; for single-lead endpoint this can be left null
    private List<Long> leadPrimeIds;

    // "FOLLOWUP" | "MEET_REMINDER" | "NORMAL_REMINDER"
    private String templateType;

    // used only when templateType = MEET_REMINDER
    private String meetingDate;
    private String meetingTime;
    private String meetingLink;

    // optional custom text, used for FOLLOWUP / NORMAL_REMINDER
    private String customMessage;

    public List<Long> getLeadPrimeIds() { return leadPrimeIds; }
    public void setLeadPrimeIds(List<Long> leadPrimeIds) { this.leadPrimeIds = leadPrimeIds; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public String getMeetingDate() { return meetingDate; }
    public void setMeetingDate(String meetingDate) { this.meetingDate = meetingDate; }

    public String getMeetingTime() { return meetingTime; }
    public void setMeetingTime(String meetingTime) { this.meetingTime = meetingTime; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getCustomMessage() { return customMessage; }
    public void setCustomMessage(String customMessage) { this.customMessage = customMessage; }
}
