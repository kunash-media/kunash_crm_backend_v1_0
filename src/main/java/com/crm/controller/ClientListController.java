package com.crm.controller;

import com.crm.dto.request.AssignClientRequest;
import com.crm.dto.request.ClientCreateRequest;
import com.crm.dto.request.ConvertLeadRequest;
import com.crm.dto.response.ClientCreatedResponse;
import com.crm.dto.stats.ClientStatsResponse;
import com.crm.service.ClientListService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/client/v1")
public class ClientListController {

    private static final Logger log = LoggerFactory.getLogger(ClientListController.class);

    private final ClientListService clientListService;

    public ClientListController(ClientListService clientListService) {
        this.clientListService = clientListService;
    }

    // Manual "Add Client" — required: firstName, contact, service. Rest optional.
    @PostMapping
    public ResponseEntity<ClientCreatedResponse> createClient(@Valid @RequestBody ClientCreateRequest request) {
        log.info("POST /api/client/v1 — create client, firstName={}", request.getFirstName());
        ClientCreatedResponse response = clientListService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Deal-done flow — single atomic transaction, see service layer.
    @PostMapping("/convert-lead/{leadPrimeId}")
    public ResponseEntity<ClientCreatedResponse> convertLead(
            @PathVariable Long leadPrimeId,
            @Valid @RequestBody ConvertLeadRequest request) {
        log.info("POST /api/client/v1/convert-lead/{} — convert request", leadPrimeId);
        ClientCreatedResponse response = clientListService.convertLeadToClient(leadPrimeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/all-clients")
    public ResponseEntity<Page<ClientCreatedResponse>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/client/v1/all-clients — page={}, size={}", page, size);
        return ResponseEntity.ok(clientListService.getAllClients(page, size));
    }

    @GetMapping("/{clientPrimeId}")
    public ResponseEntity<ClientCreatedResponse> getClientById(@PathVariable Long clientPrimeId) {
        log.info("GET /api/client/v1/{} — fetch single client", clientPrimeId);
        return ResponseEntity.ok(clientListService.getClientById(clientPrimeId));
    }

    @PatchMapping("/{clientPrimeId}")
    public ResponseEntity<ClientCreatedResponse> updateClient(
            @PathVariable Long clientPrimeId,
            @RequestBody ClientCreateRequest request) {
        log.info("PATCH /api/client/v1/{} — update client", clientPrimeId);
        return ResponseEntity.ok(clientListService.updateClient(clientPrimeId, request));
    }

    @DeleteMapping("/{clientPrimeId}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientPrimeId) {
        log.info("DELETE /api/client/v1/{} — soft delete", clientPrimeId);
        clientListService.deleteClient(clientPrimeId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        log.warn("404 — {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleConflict(IllegalStateException ex) {
        log.warn("409 — {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @GetMapping("/stats")
    public ResponseEntity<ClientStatsResponse> getClientStats() {
        log.info("GET /api/client/v1/stats — fetch stat counts");
        return ResponseEntity.ok(clientListService.getClientStats());
    }

    @PatchMapping("/{clientPrimeId}/assign")
    public ResponseEntity<ClientCreatedResponse> assignClient(
            @PathVariable Long clientPrimeId,
            @Valid @RequestBody AssignClientRequest request) {
        log.info("PATCH /api/client/v1/{}/assign — assignTo={}", clientPrimeId, request.getAssignTo());
        return ResponseEntity.ok(clientListService.assignClient(clientPrimeId, request));
    }
}