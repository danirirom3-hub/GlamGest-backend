package com.glamgest.app.application.service.auth;

import com.glamgest.app.domain.model.User;
import com.glamgest.app.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PolicyServiceTest {

    @Test
    void isAccepted_requiresCurrentPolicyVersion() {
        UserRepository repository = mock(UserRepository.class);
        PolicyService service = new PolicyService(repository, "2.0");
        User user = new User(1, "User", "user@example.com", "encoded", 1,
                "CLIENT", true, true, "1.0");

        assertFalse(service.isAccepted(user));
    }

    @Test
    void isAccepted_returnsTrueForAcceptedCurrentVersion() {
        UserRepository repository = mock(UserRepository.class);
        PolicyService service = new PolicyService(repository, "2.0");
        User user = new User(1, "User", "user@example.com", "encoded", 1,
                "CLIENT", true, true, "2.0");
        when(repository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertTrue(service.isAccepted("user@example.com"));
    }
}
