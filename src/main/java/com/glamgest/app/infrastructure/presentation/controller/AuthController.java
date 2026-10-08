package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.RegisterRequestDTO;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import com.glamgest.app.application.usecase.auth.RegisterUseCase;
import com.glamgest.app.application.dto.auth.PolicyAcceptanceRequestDTO;
import com.glamgest.app.application.dto.auth.UnlockRequestDTO;
import com.glamgest.app.application.service.auth.PolicyService;
import com.glamgest.app.application.service.auth.RecaptchaVerificationService;
import com.glamgest.app.application.service.auth.LoginHoneypotService;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.glamgest.app.application.service.auth.JwtService;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final PolicyService policyService;
    private final RecaptchaVerificationService recaptchaVerificationService;
    private final LoginHoneypotService loginHoneypotService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase, PolicyService policyService,
                          RecaptchaVerificationService recaptchaVerificationService,
                          LoginHoneypotService loginHoneypotService,
                          AuthenticationManager authenticationManager) {
        this(loginUseCase, registerUseCase, policyService, recaptchaVerificationService,
                loginHoneypotService, authenticationManager, null, null);
    }

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase, PolicyService policyService,
                          RecaptchaVerificationService recaptchaVerificationService,
                          LoginHoneypotService loginHoneypotService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.policyService = policyService;
        this.recaptchaVerificationService = recaptchaVerificationService;
        this.loginHoneypotService = loginHoneypotService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO, HttpServletRequest request) {
        loginHoneypotService.validate(loginRequestDTO.website());
        recaptchaVerificationService.verify(loginRequestDTO.recaptchaToken(), request.getRemoteAddr());
        return BuilderHelper.buildResponse(loginUseCase.execute(loginRequestDTO), "Login successful", HttpStatus.OK, true);
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlock(@Valid @RequestBody UnlockRequestDTO request,
            Authentication authentication) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authentication.getName(), request.password()));

        return BuilderHelper.buildResponse(
                java.util.Map.of("unlocked", true), "Sesión desbloqueada", HttpStatus.OK, true);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication, HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (authentication != null && header != null && header.startsWith("Bearer ")) {
            String sessionId = jwtService.extractSessionId(header.substring(7));
            userRepository.clearActiveSession(authentication.getName(), sessionId);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request, HttpServletRequest httpRequest) {
        recaptchaVerificationService.verify(request.recaptchaToken(), httpRequest.getRemoteAddr());
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
