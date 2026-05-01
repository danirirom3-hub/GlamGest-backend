package com.glamgest.app.application.service.auth;

import com.glamgest.app.application.dto.auth.LoginRequestDTO;
import com.glamgest.app.application.dto.auth.LoginResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    @Test
    void execute_whenCredentialsValid_returnsToken() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        LoginService loginService = new LoginService(authenticationManager, jwtService);

        LoginRequestDTO request = new LoginRequestDTO("user@example.com", "password123");
        UserDetails userDetails = new User("user@example.com", "password123", List.of());
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        LoginResponseDTO response = loginService.execute(request);

        assertEquals("jwt-token", response.token());
        assertEquals("Bearer", response.type());
        verify(authenticationManager).authenticate(argThat(token ->
                token instanceof UsernamePasswordAuthenticationToken
                        && "user@example.com".equals(token.getPrincipal())
                        && "password123".equals(token.getCredentials())
        ));
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void execute_whenAuthenticationFails_throwsBadCredentialsException() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        JwtService jwtService = mock(JwtService.class);
        LoginService loginService = new LoginService(authenticationManager, jwtService);

        LoginRequestDTO request = new LoginRequestDTO("user@example.com", "wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> loginService.execute(request));
    }
}
