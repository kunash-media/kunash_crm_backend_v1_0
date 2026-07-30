package com.crm.service;


import com.crm.dto.request.EmailRequestDto;
import com.crm.dto.response.BulkEmailResponseDto;
import com.crm.dto.response.EmailResultDto;

public interface EmailService {

    // works for the "1 lead selected" case too — just send a list with one id
    BulkEmailResponseDto sendBulkEmails(EmailRequestDto request);

    // dedicated single-lead endpoint (leadPrimeId comes from the path, not the body)
    EmailResultDto sendSingleEmail(Long leadPrimeId, EmailRequestDto request);
}