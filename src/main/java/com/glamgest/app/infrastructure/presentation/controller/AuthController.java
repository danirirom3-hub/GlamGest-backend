package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.RegisterRequestDTO;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import com.glamgest.app.application.usecase.auth.RegisterUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return BuilderHelper.buildResponse(loginUseCase.execute(loginRequestDTO), "Login successful", HttpStatus.OK, true);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        return BuilderHelper.buildResponse(registerUseCase.execute(request), "Registro exitoso", HttpStatus.CREATED, true);
    }
}
