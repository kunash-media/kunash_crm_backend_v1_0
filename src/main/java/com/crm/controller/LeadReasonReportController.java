package com.crm.controller;

import com.crm.dto.stats.ReasonBucketRequestDto;
import com.crm.dto.stats.ReasonReportResponseDto;
import com.crm.entity.ReasonBucketEntity;
import com.crm.enum_status.ReportGranularity;
import com.crm.service.serviceImple.LeadReasonReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/lead/v1/reports")
public class LeadReasonReportController {

    private final LeadReasonReportService reportService;

    public LeadReasonReportController(LeadReasonReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/lost-reasons")
    public ResponseEntity<ReasonReportResponseDto> getLostReasons(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Long staffPrimeId,
            @RequestParam(required = false) String requirementCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "MONTHLY") ReportGranularity granularity) {
        return ResponseEntity.ok(reportService.getLostReasonReport(
                source, staffPrimeId, requirementCategory, fromDate, toDate, granularity));
    }

    @GetMapping("/won-reasons")
    public ResponseEntity<ReasonReportResponseDto> getWonReasons(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) Long staffPrimeId,
            @RequestParam(required = false) String requirementCategory,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "MONTHLY") ReportGranularity granularity) {
        return ResponseEntity.ok(reportService.getWonReasonReport(
                source, staffPrimeId, requirementCategory, fromDate, toDate, granularity));
    }


    @PostMapping("/buckets")
    public ResponseEntity<ReasonBucketEntity> createBucket(@RequestBody ReasonBucketRequestDto dto) {
        return ResponseEntity.ok(reportService.createBucket(dto));
    }

    @PatchMapping("/buckets/{bucketPrimeId}")
    public ResponseEntity<ReasonBucketEntity> updateBucket(
            @PathVariable Long bucketPrimeId,
            @RequestBody ReasonBucketRequestDto dto) {
        return ResponseEntity.ok(reportService.updateBucket(bucketPrimeId, dto));
    }

    @PatchMapping("/buckets/{bucketPrimeId}/toggle-active")
    public ResponseEntity<ReasonBucketEntity> toggleBucketActive(@PathVariable Long bucketPrimeId) {
        return ResponseEntity.ok(reportService.toggleBucketActive(bucketPrimeId));
    }

    @GetMapping("/buckets")
    public ResponseEntity<List<ReasonBucketEntity>> getBuckets() {
        return ResponseEntity.ok(reportService.getAllBuckets());
    }
}