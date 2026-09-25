package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.auth.UnlockRequestDTO;
import com.glamgest.app.application.service.auth.LoginHoneypotService;
import com.glamgest.app.application.service.auth.PolicyService;
import com.glamgest.app.application.service.auth.RecaptchaVerificationService;
import com.glamgest.app.application.usecase.auth.LoginUseCase;
import com.glamgest.app.application.usecase.auth.RegisterUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void unlock_whenPasswordIsValid_returnsSuccess() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        AuthController controller = controllerWith(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        var response = controller.unlock(new UnlockRequestDTO("password123"), authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void unlock_whenPasswordIsInvalid_propagatesBadCredentials() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));
        AuthController controller = controllerWith(authenticationManager);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        assertThrows(BadCredentialsException.class,
                () -> controller.unlock(new UnlockRequestDTO("wrong-password"), authentication));
    }

    private AuthController controllerWith(AuthenticationManager authenticationManager) {
        return new AuthController(
                mock(LoginUseCase.class),
                mock(RegisterUseCase.class),
                mock(PolicyService.class),
                mock(RecaptchaVerificationService.class),
                mock(LoginHoneypotService.class),
                authenticationManager);
    }
}
