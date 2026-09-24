package com.glamgest.app.application.dto.auth;

public record LoginResponseDTO(String token, String type, String role, Integer userId, Integer clientId,
        boolean privacyPolicyAccepted, String privacyPolicyVersion, boolean privacyPolicyRequired) {
    public LoginResponseDTO(String token) {
        this(token, "Bearer", null, null, null, false, null, false);
    }

    public LoginResponseDTO(String token, String type) {
        this(token, type, null, null, null, false, null, false);
    }
}
