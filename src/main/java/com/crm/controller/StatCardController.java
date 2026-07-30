package com.crm.controller;

import com.crm.dto.stats.LeadStatsResponse;
import com.crm.service.StatCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stat/v1")
public class StatCardController {

    private static final Logger log = LoggerFactory.getLogger(StatCardController.class);

    private final StatCardService statCardService;

    public StatCardController(StatCardService statCardService) {
        this.statCardService = statCardService;
    }

    @GetMapping("/lead-stats")
    public ResponseEntity<LeadStatsResponse> getLeadStats() {
        log.info("GET /api/stat/v1/lead-stats — fetch dashboard stat card counts");
        return ResponseEntity.ok(statCardService.getLeadStats());
    }
}