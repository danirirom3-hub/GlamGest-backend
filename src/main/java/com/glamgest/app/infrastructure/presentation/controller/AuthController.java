package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.LoginResponseDTO;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return BuilderHelper.buildResponse(loginUseCase.execute(loginRequestDTO), "Login successful", HttpStatus.OK, true);
    }
}