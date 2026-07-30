package com.crm.controller;

import com.crm.dto.request.EmailRequestDto;
import com.crm.dto.request.LeadFollowupRequestDto;
import com.crm.dto.request.LeadOutcomeRequest;
import com.crm.dto.request.LeadRequestDto;
import com.crm.dto.response.BulkEmailResponseDto;
import com.crm.dto.response.EmailResultDto;
import com.crm.dto.response.LeadResponseDto;
import com.crm.dto.stats.MonthlyLeadCountDto;
import com.crm.service.EmailService;
import com.crm.service.LeadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/lead/v1")
public class LeadController {

    private static final Logger log = LoggerFactory.getLogger(LeadController.class);
    private final LeadService leadService;
    private final EmailService emailService;

    @Autowired
    public LeadController(LeadService leadService, EmailService emailService) {
        this.leadService = leadService;
        this.emailService = emailService;
    }

    // CREATE
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LeadResponseDto> createLead(
            @RequestPart("lead") LeadRequestDto leadRequestDto,
            @RequestPart(value = "docFile", required = false) MultipartFile docFile) {
        LeadResponseDto response = leadService.createLead(leadRequestDto, docFile);
        return ResponseEntity.ok(response);
    }

    // UPDATE
    @PatchMapping(value = "/{leadPrimeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LeadResponseDto> updateLead(
            @PathVariable Long leadPrimeId,
            @RequestPart("lead") LeadRequestDto leadRequestDto,
            @RequestPart(value = "docFile", required = false) MultipartFile docFile) {
        LeadResponseDto response = leadService.updateLead(leadPrimeId, leadRequestDto, docFile);
        return ResponseEntity.ok(response);
    }

    // GET by leadPrimeId
    @GetMapping("/{leadPrimeId}")
    public ResponseEntity<LeadResponseDto> getLeadByPrimeId(@PathVariable Long leadPrimeId) {
        return ResponseEntity.ok(leadService.getLeadByPrimeId(leadPrimeId));
    }

    // GET by leadStrId
    @GetMapping("/str/{leadStrId}")
    public ResponseEntity<LeadResponseDto> getLeadByStrId(@PathVariable String leadStrId) {
        return ResponseEntity.ok(leadService.getLeadByStrId(leadStrId));
    }

    // GET all with pagination
    @GetMapping("/all-leads")
    public ResponseEntity<Page<LeadResponseDto>> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(leadService.getAllLeads(page, size));
    }

    // DELETE (soft delete)
    @DeleteMapping("/delete-lead/{leadPrimeId}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long leadPrimeId) {
        leadService.deleteLead(leadPrimeId);
        return ResponseEntity.noContent().build();
    }

    // GET actual doc file
    @GetMapping("/{leadPrimeId}/docFile")
    public ResponseEntity<byte[]> getDocFile(@PathVariable Long leadPrimeId) {
        byte[] fileBytes = leadService.getDocFileBytes(leadPrimeId);
        String fileType = leadService.getDocFileType(leadPrimeId);
        String fileName = leadService.getDocFileName(leadPrimeId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(fileBytes);
    }

    // Add a followup entry
    @PostMapping("/add/{leadPrimeId}/followup")
    public ResponseEntity<LeadResponseDto> addFollowup(
            @PathVariable Long leadPrimeId,
            @RequestBody LeadFollowupRequestDto dto) {
        return ResponseEntity.ok(leadService.addFollowup(leadPrimeId, dto));
    }

    // Get all followups of a lead (with id + createdAt, for history display)
    @GetMapping("/get-all/{leadPrimeId}/followup")
    public ResponseEntity<List<LeadFollowupRequestDto>> getFollowups(@PathVariable Long leadPrimeId) {
        return ResponseEntity.ok(leadService.getFollowups(leadPrimeId));
    }

    // Mark a lead as converted (won) — excludes it from the main pipeline table
    @PatchMapping("/convert/{leadPrimeId}")
    public ResponseEntity<LeadResponseDto> convertLead(@PathVariable Long leadPrimeId) {
        return ResponseEntity.ok(leadService.convertLead(leadPrimeId));
    }

    // Bulk soft-delete
    @DeleteMapping("/delete-bulk")
    public ResponseEntity<Void> deleteBulk(@RequestBody List<Long> leadPrimeIds) {
        leadService.deleteBulk(leadPrimeIds);
        return ResponseEntity.noContent().build();
    }

    // Real-time duplicate check as user types/blurs the phone field.
    // 204 = no existing lead found; 200 = existing lead returned.
    @GetMapping("/check-phone")
    public ResponseEntity<LeadResponseDto> checkPhone(@RequestParam String phone) {
        LeadResponseDto existing = leadService.checkPhoneExists(phone);
        if (existing == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(existing);
    }

    // Detailed followup history (id + createdAt) — used by the History overlay
    @GetMapping("/history/{leadPrimeId}/followup")
    public ResponseEntity<List<com.crm.dto.response.LeadFollowupResponseDto>> getFollowupHistory(@PathVariable Long leadPrimeId) {
        return ResponseEntity.ok(leadService.getFollowupsDetailed(leadPrimeId));
    }

    // Month-wise lead count — used for the dashboard bar chart.
    // Returns a fixed-length, chronologically-ordered list (oldest to newest),
    // including months with zero leads so the chart's x-axis stays continuous.
    @GetMapping("/monthly-count")
    public ResponseEntity<List<MonthlyLeadCountDto>> getMonthlyLeadCounts(
            @RequestParam(defaultValue = "6") int monthsBack) {
        return ResponseEntity.ok(leadService.getMonthlyLeadCounts(monthsBack));
    }

    @PostMapping("/{leadPrimeId}/send-email")
    public ResponseEntity<EmailResultDto> sendSingleEmail(
            @PathVariable Long leadPrimeId,
            @RequestBody EmailRequestDto emailRequestDto) {
        EmailResultDto result = emailService.sendSingleEmail(leadPrimeId, emailRequestDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/send-email-bulk")
    public ResponseEntity<BulkEmailResponseDto> sendBulkEmail(@RequestBody EmailRequestDto emailRequestDto) {
        BulkEmailResponseDto result = emailService.sendBulkEmails(emailRequestDto);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{leadPrimeId}/outcome")
    public ResponseEntity<Void> updateLeadOutcome(
            @PathVariable Long leadPrimeId,
            @Valid @RequestBody LeadOutcomeRequest request) {
        log.info("PATCH /api/lead/v1/{}/outcome — outcome={}", leadPrimeId, request.getLeadOutcome());
        leadService.updateLeadOutcome(leadPrimeId, request.getLeadOutcome(), request.getLostReason());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/by-outcome")
    public ResponseEntity<Page<LeadResponseDto>> getLeadsByOutcome(
            @RequestParam String outcome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/lead/v1/by-outcome — outcome={}, page={}, size={}", outcome, page, size);
        return ResponseEntity.ok(leadService.getLeadsByOutcome(outcome, page, size));
    }

    // Add a followup entry
}