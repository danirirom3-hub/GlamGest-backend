package com.glamgest.app.application.dto.auth;

public record LoginResponseDTO(String token, String type) {
    public LoginResponseDTO(String token) {
        this(token, "Bearer");
    }
}