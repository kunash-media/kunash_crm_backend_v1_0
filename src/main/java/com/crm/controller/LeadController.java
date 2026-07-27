package com.crm.controller;

import com.crm.dto.request.LeadFollowupRequestDto;
import com.crm.dto.request.LeadRequestDto;
import com.crm.dto.response.LeadResponseDto;
import com.crm.service.LeadService;
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

    private final LeadService leadService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
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
    @GetMapping
    public ResponseEntity<Page<LeadResponseDto>> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(leadService.getAllLeads(page, size));
    }

    // DELETE (soft delete)
    @DeleteMapping("/{leadPrimeId}")
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
    @PostMapping("/{leadPrimeId}/followup")
    public ResponseEntity<LeadResponseDto> addFollowup(
            @PathVariable Long leadPrimeId,
            @RequestBody LeadFollowupRequestDto dto) {
        return ResponseEntity.ok(leadService.addFollowup(leadPrimeId, dto));
    }

    // Get all followups of a lead
    @GetMapping("/{leadPrimeId}/followup")
    public ResponseEntity<List<LeadFollowupRequestDto>> getFollowups(@PathVariable Long leadPrimeId) {
        return ResponseEntity.ok(leadService.getFollowups(leadPrimeId));
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

    // Add a followup entry
}