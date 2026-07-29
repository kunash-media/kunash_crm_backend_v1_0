package com.crm.util;

import com.crm.config.GoogleOAuthConfig;
import com.crm.entity.GoogleAuthTokenEntity;
import com.crm.repository.GoogleAuthTokenRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleAuthService {

    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/calendar.events",
            "https://www.googleapis.com/auth/userinfo.email"
    );

    private final GoogleOAuthConfig config;
    private final GoogleAuthTokenRepository tokenRepository;

    @Autowired
    public GoogleAuthService(GoogleOAuthConfig config, GoogleAuthTokenRepository tokenRepository) {
        this.config = config;
        this.tokenRepository = tokenRepository;
    }

    private GoogleAuthorizationCodeFlow buildFlow() throws GeneralSecurityException, IOException {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(config.getClientId());
        details.setClientSecret(config.getClientSecret());

        GoogleClientSecrets secrets = new GoogleClientSecrets().setWeb(details);

        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                secrets,
                SCOPES
        )
                .setAccessType("offline")   // required to receive a refresh token
                .setApprovalPrompt("force") // forces Google to re-issue a refresh token every time
                .build();
    }

    /** Step A: builds the URL the user visits to grant consent. */
    public String buildConsentUrl() {
        try {
            return buildFlow().newAuthorizationUrl()
                    .setRedirectUri(config.getRedirectUri())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to build Google consent URL", e);
        }
    }

    /** Step B: exchanges the authorization code (from the callback) for tokens, and stores them. */
    public void handleCallback(String code) {
        try {
            GoogleAuthorizationCodeFlow flow = buildFlow();
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(config.getRedirectUri())
                    .execute();

            String accessToken = tokenResponse.getAccessToken();
            String refreshToken = tokenResponse.getRefreshToken();
            Long expiresInSeconds = tokenResponse.getExpiresInSeconds();

            // fetch the connected account's email so we can label the stored row
            String email = fetchAccountEmail(accessToken);

            GoogleAuthTokenEntity entity = tokenRepository.findFirstByOrderByIdAsc().orElse(new GoogleAuthTokenEntity());
            entity.setAccountEmail(email);
            entity.setAccessToken(accessToken);
            if (refreshToken != null) {
                // Google only sends a refresh token on the FIRST consent (or when approval_prompt=force) —
                // don't overwrite an existing one with null on subsequent token refreshes.
                entity.setRefreshToken(refreshToken);
            }
            entity.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds != null ? expiresInSeconds : 3600));

            tokenRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange Google authorization code", e);
        }
    }

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);

    private String fetchAccountEmail(String accessToken) {
        try {
            java.net.URL url = new java.net.URL("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken);
            try (java.io.InputStream is = url.openStream()) {
                String json = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                return obj.has("email") ? obj.get("email").getAsString() : "unknown";
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Google account email: {}", e.getMessage());
            return "unknown";
        }
    }

    public boolean isConnected() {
        return tokenRepository.findFirstByOrderByIdAsc().isPresent();
    }

    /** Returns a valid (non-expired) access token, refreshing it via the refresh_token if needed. */
    public String getValidAccessToken() {
        GoogleAuthTokenEntity entity = tokenRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Google account not connected. Visit /oauth2/connect first."));

        boolean expired = entity.getAccessTokenExpiresAt() == null
                || entity.getAccessTokenExpiresAt().isBefore(LocalDateTime.now().plusMinutes(2)); // refresh a bit early

        if (!expired) {
            return entity.getAccessToken();
        }

        if (entity.getRefreshToken() == null) {
            throw new IllegalStateException("No refresh token stored — reconnect via /oauth2/connect.");
        }

        try {
            com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest refreshRequest =
                    new com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest(
                            new NetHttpTransport(),
                            GsonFactory.getDefaultInstance(),
                            entity.getRefreshToken(),
                            config.getClientId(),
                            config.getClientSecret()
                    );

            GoogleTokenResponse refreshed = refreshRequest.execute();

            entity.setAccessToken(refreshed.getAccessToken());
            Long expiresIn = refreshed.getExpiresInSeconds();
            entity.setAccessTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn != null ? expiresIn : 3600));
            tokenRepository.save(entity);

            return entity.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh Google access token", e);
        }
    }
}