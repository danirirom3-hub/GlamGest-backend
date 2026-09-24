package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.RegisterRequestDTO;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import com.glamgest.app.application.usecase.auth.RegisterUseCase;
import com.glamgest.app.application.dto.auth.PolicyAcceptanceRequestDTO;
import com.glamgest.app.application.service.auth.PolicyService;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final PolicyService policyService;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase, PolicyService policyService) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.policyService = policyService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return BuilderHelper.buildResponse(loginUseCase.execute(loginRequestDTO), "Login successful", HttpStatus.OK, true);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        return BuilderHelper.buildResponse(registerUseCase.execute(request), "Registro exitoso", HttpStatus.CREATED, true);
    }

    @GetMapping("/policy")
    public ResponseEntity<?> getPolicy() {
        return BuilderHelper.buildResponse(policyService.getPolicy(), "Política de tratamiento de datos", HttpStatus.OK, true);
    }

    @PutMapping("/policy")
    public ResponseEntity<?> acceptPolicy(@Valid @RequestBody PolicyAcceptanceRequestDTO request,
            Authentication authentication) {
        policyService.accept(authentication.getName());
        return BuilderHelper.buildResponse(
                java.util.Map.of("accepted", true, "version", policyService.currentVersion()),
                "Política aceptada", HttpStatus.OK, true);
    }
}
