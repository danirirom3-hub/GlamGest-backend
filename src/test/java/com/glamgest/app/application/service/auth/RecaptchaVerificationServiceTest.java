package com.glamgest.app.application.service.auth;

import com.glamgest.app.common.exception.InvalidRecaptchaException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecaptchaVerificationServiceTest {

    @Test
    void verify_whenGoogleAcceptsToken_doesNotThrow() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.postForObject(eq("http://localhost/recaptcha"), any(),
                eq(RecaptchaVerificationService.RecaptchaResponse.class)))
                .thenReturn(new RecaptchaVerificationService.RecaptchaResponse(true, null));

        RecaptchaVerificationService service = new RecaptchaVerificationService(
                restTemplate, "secret", "http://localhost/recaptcha");

        assertDoesNotThrow(() -> service.verify("token", "127.0.0.1"));
    }

    @Test
    void verify_whenGoogleRejectsToken_throwsInvalidRecaptchaException() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.postForObject(any(), any(), eq(RecaptchaVerificationService.RecaptchaResponse.class)))
                .thenReturn(new RecaptchaVerificationService.RecaptchaResponse(false, null));

        RecaptchaVerificationService service = new RecaptchaVerificationService(restTemplate, "secret", "url");

        assertThrows(InvalidRecaptchaException.class, () -> service.verify("token", null));
    }

    @Test
    void verify_whenGoogleIsUnavailable_throwsInvalidRecaptchaException() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.postForObject(any(), any(), eq(RecaptchaVerificationService.RecaptchaResponse.class)))
                .thenThrow(new RestClientException("timeout"));

        RecaptchaVerificationService service = new RecaptchaVerificationService(restTemplate, "secret", "url");

        assertThrows(InvalidRecaptchaException.class, () -> service.verify("token", null));
    }

    @Test
    void verify_whenTokenIsBlank_throwsInvalidRecaptchaException() {
        RecaptchaVerificationService service = new RecaptchaVerificationService(
                mock(RestTemplate.class), "secret", "url");

        assertThrows(InvalidRecaptchaException.class, () -> service.verify(" ", null));
    }
}
