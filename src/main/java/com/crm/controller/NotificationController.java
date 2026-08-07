package com.crm.controller;


import com.crm.dto.stats.PendingPaymentAlertResponse;
import com.crm.entity.NotificationStateEntity;
import com.crm.repository.NotificationRepository;
import com.crm.service.ClientListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final ClientListService clientListService;
    private final NotificationRepository notificationRepository;

    public NotificationController(ClientListService clientListService,
                                  NotificationRepository notificationRepository) {
        this.clientListService = clientListService;
        this.notificationRepository = notificationRepository;
    }

    // ── GET all alerts (currently: pending payments) with visited status ──
    @GetMapping("/alerts")
    public ResponseEntity<List<Map<String, Object>>> getAlerts(@RequestParam String adminId) {
        log.info("GET /api/notifications/alerts — adminId={}", adminId);

        Set<String> visitedFingerprints = notificationRepository.findByAdminId(adminId)
                .stream()
                .map(NotificationStateEntity::getFingerprint)
                .collect(Collectors.toSet());

        List<PendingPaymentAlertResponse> pendingAlerts = clientListService.getPendingPaymentAlerts();

        List<Map<String, Object>> result = pendingAlerts.stream()
                .map(alert -> {
                    String fingerprint = "pending-" + alert.getClientStrId();
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("fingerprint", fingerprint);
                    item.put("type", "PENDING_PAYMENT");
                    item.put("clientPrimeId", alert.getClientPrimeId());
                    item.put("clientStrId", alert.getClientStrId());
                    item.put("clientName", alert.getClientName());
                    item.put("contact", alert.getContact());
                    item.put("totalAmount", alert.getTotalAmount());
                    item.put("advanceAmount", alert.getAdvanceAmount());
                    item.put("pendingAmount", alert.getPendingAmount());
                    item.put("severity", alert.getSeverity());
                    item.put("createdAt", alert.getCreatedAt());
                    item.put("visited", visitedFingerprints.contains(fingerprint));
                    return item;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ── GET unread count ──
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam String adminId) {
        log.info("GET /api/notifications/unread-count — adminId={}", adminId);

        Set<String> visitedFingerprints = notificationRepository.findByAdminId(adminId)
                .stream()
                .map(NotificationStateEntity::getFingerprint)
                .collect(Collectors.toSet());

        long unread = clientListService.getPendingPaymentAlerts().stream()
                .filter(alert -> !visitedFingerprints.contains("pending-" + alert.getClientStrId()))
                .count();

        return ResponseEntity.ok(Map.of("count", unread));
    }

    // ── POST mark a single alert as visited ──
    @PostMapping("/visit")
    public ResponseEntity<Void> markVisited(@RequestParam String adminId,
                                            @RequestParam String fingerprint) {
        log.info("POST /api/notifications/visit — adminId={}, fingerprint={}", adminId, fingerprint);
        if (!notificationRepository.existsByAdminIdAndFingerprint(adminId, fingerprint)) {
            notificationRepository.save(new NotificationStateEntity(adminId, fingerprint));
        }
        return ResponseEntity.ok().build();
    }

    // ── POST mark all current alerts as visited ──
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead(@RequestParam String adminId) {
        log.info("POST /api/notifications/mark-all-read — adminId={}", adminId);

        clientListService.getPendingPaymentAlerts().forEach(alert -> {
            String fingerprint = "pending-" + alert.getClientStrId();
            if (!notificationRepository.existsByAdminIdAndFingerprint(adminId, fingerprint)) {
                notificationRepository.save(new NotificationStateEntity(adminId, fingerprint));
            }
        });

        return ResponseEntity.ok().build();
    }
}
