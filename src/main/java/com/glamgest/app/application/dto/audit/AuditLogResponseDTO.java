package com.glamgest.app.application.dto.audit;

import java.time.LocalDateTime;

public record AuditLogResponseDTO(
        Long id,
        LocalDateTime occurredAt,
        Integer userId,
        String userEmail,
        String role,
        String action,
        String module,
        String resourceId,
        String httpMethod,
        String endpoint,
        Integer statusCode,
        String description,
        String ipAddress,
        String userAgent,
        String errorMessage) {
}
