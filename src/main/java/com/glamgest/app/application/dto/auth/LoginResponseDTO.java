package com.glamgest.app.application.dto.auth;

public record LoginResponseDTO(String token, String type, String role, Integer userId, Integer clientId) {
    public LoginResponseDTO(String token) {
        this(token, "Bearer", null, null, null);
    }

    public LoginResponseDTO(String token, String type) {
        this(token, type, null, null, null);
    }
}
