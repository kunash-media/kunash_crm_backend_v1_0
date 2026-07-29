package com.crm.controller;

import com.crm.util.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/oauth2")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    @Autowired
    public GoogleAuthController(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    // Step A — hit this from the frontend (or paste in browser) to start the connect flow
    @GetMapping("/connect")
    public ResponseEntity<Void> connect() {
        String consentUrl = googleAuthService.buildConsentUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(consentUrl))
                .build();
    }

    // Step B — Google redirects here automatically after the user approves consent
    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        googleAuthService.handleCallback(code);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html")
                .body("<html><body style='font-family:sans-serif;text-align:center;padding-top:80px;'>"
                        + "<h2>Google account connected successfully</h2>"
                        + "<p>You can close this tab and return to Kunash CRM.</p>"
                        + "</body></html>");
    }

    // quick status check — used later by the frontend to show connected/not-connected state
    @GetMapping("/status")
    public ResponseEntity<Boolean> status() {
        return ResponseEntity.ok(googleAuthService.isConnected());
    }
}