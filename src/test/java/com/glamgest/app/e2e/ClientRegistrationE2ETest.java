package com.glamgest.app.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.glamgest.app.application.service.auth.RecaptchaVerificationService;
import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClientRegistrationE2ETest {

    @Autowired
    private TestRestTemplate http;

    @Autowired
    private RoleRepository roleRepository;

    // Google reCAPTCHA es un servicio externo; el flujo de aplicación se prueba sin depender de él.
    @MockitoBean
    private RecaptchaVerificationService recaptchaVerificationService;

    @BeforeEach
    void ensureClientRoleExists() {
        roleRepository.findByName("CLIENT")
                .orElseGet(() -> roleRepository.save(new Role(null, "CLIENT", "Cliente")));
    }

    @Test
    void registerLoginAndReadOwnProfile_overHttp() {
        String email = "e2e-" + UUID.randomUUID() + "@example.com";
        String password = "Password123!";
        String phone = "+57300" + ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);

        ResponseEntity<JsonNode> anonymousProfile = http.getForEntity("/api/clients/me", JsonNode.class);
        assertEquals(403, anonymousProfile.getStatusCode().value(),
                "El perfil debe requerir autenticación");

        ResponseEntity<JsonNode> registration = http.postForEntity("/api/auth/register", Map.of(
                "name", "Cliente E2E",
                "email", email,
                "password", password,
                "phone", phone,
                "privacyPolicyAccepted", true,
                "recaptchaToken", "test-token"
        ), JsonNode.class);

        assertEquals(201, registration.getStatusCode().value());
        JsonNode registrationData = requiredData(registration);
        assertEquals("CLIENT", registrationData.path("role").asText());
        assertTrue(registrationData.path("privacyPolicyAccepted").asBoolean());
        assertTrue(registrationData.path("token").isTextual());

        ResponseEntity<JsonNode> login = http.postForEntity("/api/auth/login", Map.of(
                "email", email,
                "password", password,
                "recaptchaToken", "test-token",
                "website", ""
        ), JsonNode.class);

        assertEquals(200, login.getStatusCode().value());
        JsonNode loginData = requiredData(login);
        assertEquals("CLIENT", loginData.path("role").asText());
        assertFalse(loginData.path("privacyPolicyRequired").asBoolean());
        String token = loginData.path("token").asText();
        assertFalse(token.isBlank());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<JsonNode> profile = http.exchange(
                "/api/clients/me", HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);

        assertEquals(200, profile.getStatusCode().value());
        JsonNode profileData = requiredData(profile);
        assertEquals(email, profileData.path("email").asText());
        assertEquals("Cliente E2E", profileData.path("name").asText());
        assertEquals(phone, profileData.path("phone").asText());
    }

    private JsonNode requiredData(ResponseEntity<JsonNode> response) {
        assertNotNull(response.getBody(), "La respuesta HTTP debe incluir JSON");
        JsonNode data = response.getBody().path("data");
        assertFalse(data.isMissingNode(), "La respuesta debe incluir la propiedad data");
        return data;
    }
}
