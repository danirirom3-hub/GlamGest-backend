package com.glamgest.app.application.service.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class LoginHoneypotService {

    public void validate(String value) {
        if (value != null && !value.isBlank()) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}
