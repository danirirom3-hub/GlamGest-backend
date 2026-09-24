package com.glamgest.app.application.service.auth;

import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import com.glamgest.app.application.dto.auth.PrivacyPolicyResponseDTO;

@Service
public class PolicyService {

    private final UserRepository userRepository;
    private final String currentVersion;

    public PolicyService(UserRepository userRepository,
            @Value("${privacy-policy.version:1.0}") String currentVersion) {
        this.userRepository = userRepository;
        this.currentVersion = currentVersion;
    }

    public String currentVersion() {
        return currentVersion;
    }

    public PrivacyPolicyResponseDTO getPolicy() {
        try {
            String content = new String(new ClassPathResource("privacy-policy.md")
                    .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return new PrivacyPolicyResponseDTO(currentVersion, "2026-09-24", content);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar la política de privacidad", ex);
        }
    }

    public boolean isAccepted(User user) {
        return Boolean.TRUE.equals(user.getPrivacyPolicyAccepted())
                && currentVersion.equals(user.getPrivacyPolicyVersion());
    }

    public boolean isAccepted(String email) {
        return userRepository.findByEmail(email).map(this::isAccepted).orElse(false);
    }

    @Transactional
    public void accept(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        userRepository.save(new User(user.getId(), user.getName(), user.getEmail(), user.getPassword(),
                user.getRoleId(), user.getRoleName(), user.getActive(), true, currentVersion, LocalDateTime.now()));
    }
}
