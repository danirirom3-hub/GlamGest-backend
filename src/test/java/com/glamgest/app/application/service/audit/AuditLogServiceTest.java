package com.glamgest.app.application.service.audit;

import com.glamgest.app.domain.repository.UserRepository;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
import com.glamgest.app.infrastructure.persistence.entity.AuditLogs;
import com.glamgest.app.infrastructure.persistence.repository.JpaAuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private JpaAuditLogRepository repository;

    @Mock
    private UserRepository userRepository;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogService(repository, userRepository, 365);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void recordsTheResourceIdentityProvidedByTheSuccessfulWrite() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/clients");
        request.setAttribute(AuditLogContext.RESOURCE_ID_ATTRIBUTE, "27");
        request.setAttribute(AuditLogContext.RESOURCE_NAME_ATTRIBUTE, "Ana López");

        auditLogService.record(request, 201, null);

        ArgumentCaptor<AuditLogs> savedLog = ArgumentCaptor.forClass(AuditLogs.class);
        verify(repository).save(savedLog.capture());
        assertEquals("CREATE", savedLog.getValue().getAction());
        assertEquals("clients", savedLog.getValue().getModule());
        assertEquals("27", savedLog.getValue().getResourceId());
        assertEquals("Ana López", savedLog.getValue().getResourceName());
        org.junit.jupiter.api.Assertions.assertTrue(savedLog.getValue().getDescription().contains("Ana López"));
    }
}
