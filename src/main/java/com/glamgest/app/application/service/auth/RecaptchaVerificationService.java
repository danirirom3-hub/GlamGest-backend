package com.glamgest.app.application.service.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.glamgest.app.common.exception.InvalidRecaptchaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RecaptchaVerificationService {

    private static final String INVALID_MESSAGE = "La verificación reCAPTCHA no es válida";

    private final RestTemplate restTemplate;
    private final String secretKey;
    private final String verifyUrl;

    public RecaptchaVerificationService(
            RestTemplate restTemplate,
            @Value("${google.recaptcha.secret-key:}") String secretKey,
            @Value("${google.recaptcha.verify-url:https://www.google.com/recaptcha/api/siteverify}") String verifyUrl) {
        this.restTemplate = restTemplate;
        this.secretKey = secretKey;
        this.verifyUrl = verifyUrl;
    }

    public void verify(String token, String remoteIp) {
        if (token == null || token.isBlank() || secretKey.isBlank()) {
            throw new InvalidRecaptchaException(INVALID_MESSAGE);
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", token);
        if (remoteIp != null && !remoteIp.isBlank()) {
            form.add("remoteip", remoteIp);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            RecaptchaResponse response = restTemplate.postForObject(
                    verifyUrl, new HttpEntity<>(form, headers), RecaptchaResponse.class);
            if (response == null || !response.success()) {
                throw new InvalidRecaptchaException(INVALID_MESSAGE);
            }
        } catch (InvalidRecaptchaException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new InvalidRecaptchaException(INVALID_MESSAGE, ex);
        }
    }

    record RecaptchaResponse(
            boolean success,
            @JsonProperty("error-codes") List<String> errorCodes) {
    }
}
