package com.crm.service;

import com.crm.dto.request.LeadFollowupRequestDto;
import com.crm.dto.request.LeadRequestDto;
import com.crm.dto.response.BulkUploadResult;
import com.crm.dto.response.LeadFollowupResponseDto;
import com.crm.dto.response.LeadResponseDto;
import com.crm.dto.response.LeadSuggestionDto;
import com.crm.dto.stats.MonthlyLeadCountDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LeadService {

    LeadResponseDto createLead(LeadRequestDto dto, MultipartFile docFile);

    LeadResponseDto updateLead(Long leadPrimeId, LeadRequestDto dto, MultipartFile docFile);

    LeadResponseDto getLeadByPrimeId(Long leadPrimeId);

    LeadResponseDto getLeadByStrId(String leadStrId);

    Page<LeadResponseDto> getAllLeads(int page, int size);

    void deleteLead(Long leadPrimeId);

    byte[] getDocFileBytes(Long leadPrimeId);

    String getDocFileType(Long leadPrimeId);

    String getDocFileName(Long leadPrimeId);

    LeadResponseDto addFollowup(Long leadPrimeId, LeadFollowupRequestDto dto);

    List<LeadFollowupRequestDto> getFollowups(Long leadPrimeId);

    LeadResponseDto updateFollowup(Long leadPrimeId, Long followupPrimeId, LeadFollowupRequestDto dto);

    LeadResponseDto deleteFollowup(Long leadPrimeId, Long followupPrimeId);

    // returns the existing lead if the phone number is already in use, else null
    LeadResponseDto checkPhoneExists(String phone);

    LeadResponseDto convertLead(Long leadPrimeId);

    void deleteBulk(List<Long> leadPrimeIds);

    List<LeadFollowupResponseDto> getFollowupsDetailed(Long leadPrimeId);

    List<MonthlyLeadCountDto> getMonthlyLeadCounts(int monthsBack);

    void updateLeadOutcome(Long leadPrimeId, String outcome, String lostReason);

    Page<LeadResponseDto> getLeadsByOutcome(String outcome, int page, int size);

    BulkUploadResult bulkUploadLeads(MultipartFile file) throws Exception;

    List<LeadSuggestionDto> searchLeadSuggestions(String query, int limit);

}