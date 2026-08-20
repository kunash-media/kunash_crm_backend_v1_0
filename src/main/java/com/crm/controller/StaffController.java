package com.crm.controller;

import com.crm.dto.request.PasswordChangeRequest;
import com.crm.dto.response.PagedResponse;
import com.crm.dto.request.StaffPatchRequest;
import com.crm.dto.request.StaffRegisterRequest;
import com.crm.dto.response.StaffDropdownDto;
import com.crm.dto.response.StaffRegisterdResponse;
import com.crm.dto.stats.StaffStatsDto;
import com.crm.service.StaffService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

    private static final Logger logger = LoggerFactory.getLogger(StaffController.class);
    private static final String TRACE_ID = "traceId";

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/register-staff")
    public ResponseEntity<StaffRegisterdResponse> createStaff(@Valid @RequestBody StaffRegisterRequest request) {
        setTraceId();
        try {
            logger.info("POST /api/v1/staff — request received to create staff, email={}", request.getStaffEmail());
            StaffRegisterdResponse response = staffService.createStaff(request);
            logger.info("POST /api/v1/staff — completed, staffPrimeId={}", response.getStaffPrimeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } finally {
            clearTraceId();
        }
    }

    @GetMapping("/{staffPrimeId}")
    public ResponseEntity<StaffRegisterdResponse> getStaffById(@PathVariable Long staffPrimeId) {
        setTraceId();
        try {
            logger.info("GET /api/v1/staff/{} — request received", staffPrimeId);
            StaffRegisterdResponse response = staffService.getStaffById(staffPrimeId);
            logger.info("GET /api/v1/staff/{} — completed", staffPrimeId);
            return ResponseEntity.ok(response);
        } finally {
            clearTraceId();
        }
    }

    @GetMapping
    public ResponseEntity<PagedResponse<StaffRegisterdResponse>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        setTraceId();
        try {
            logger.info("GET /api/v1/staff — request received, page={}, size={}", page, size);
            PagedResponse<StaffRegisterdResponse> response = staffService.getAllStaff(page, size);
            logger.info("GET /api/v1/staff — completed, returned={} of total={}",
                    response.getContent().size(), response.getTotalElements());
            return ResponseEntity.ok(response);
        } finally {
            clearTraceId();
        }
    }

    @PatchMapping("/{staffPrimeId}")
    public ResponseEntity<StaffRegisterdResponse> patchStaff(
            @PathVariable Long staffPrimeId,
            @RequestBody StaffPatchRequest request) {
        setTraceId();
        try {
            logger.info("PATCH /api/v1/staff/{} — request received", staffPrimeId);
            StaffRegisterdResponse response = staffService.patchStaff(staffPrimeId, request);
            logger.info("PATCH /api/v1/staff/{} — completed", staffPrimeId);
            return ResponseEntity.ok(response);
        } finally {
            clearTraceId();
        }
    }

    @DeleteMapping("/{staffPrimeId}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long staffPrimeId) {
        setTraceId();
        try {
            logger.info("DELETE /api/v1/staff/{} — request received", staffPrimeId);
            staffService.softDeleteStaff(staffPrimeId);
            logger.info("DELETE /api/v1/staff/{} — completed", staffPrimeId);
            return ResponseEntity.noContent().build();
        } finally {
            clearTraceId();
        }
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<StaffDropdownDto>> getStaffDropdownList() {
        setTraceId();
        try {
            logger.info("GET /api/v1/staff/dropdown — request received");
            List<StaffDropdownDto> response = staffService.getStaffDropdownList();
            logger.info("GET /api/v1/staff/dropdown — completed, count={}", response.size());
            return ResponseEntity.ok(response);
        } finally {
            clearTraceId();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<StaffStatsDto> getStaffStats() {
        setTraceId();
        try {
            logger.info("GET /api/v1/staff/stats — request received");
            StaffStatsDto response = staffService.getStaffStats();
            logger.info("GET /api/v1/staff/stats — completed");
            return ResponseEntity.ok(response);
        } finally {
            clearTraceId();
        }
    }


    @PatchMapping("/{staffPrimeId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long staffPrimeId,
            @RequestBody PasswordChangeRequest request) {
        setTraceId();
        try {
            logger.info("PATCH /api/v1/staff/{}/password — request received", staffPrimeId);
            staffService.changePassword(staffPrimeId, request.getOldPassword(), request.getNewPassword());
            logger.info("PATCH /api/v1/staff/{}/password — completed", staffPrimeId);
            return ResponseEntity.noContent().build();
        } finally {
            clearTraceId();
        }
    }


    private void setTraceId() {
        MDC.put(TRACE_ID, UUID.randomUUID().toString());
    }

    private void clearTraceId() {
        MDC.remove(TRACE_ID);
    }


}