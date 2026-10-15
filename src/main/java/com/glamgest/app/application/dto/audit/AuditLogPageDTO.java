package com.glamgest.app.application.dto.audit;

import java.util.List;

public record AuditLogPageDTO(
        List<AuditLogResponseDTO> items,
        int page,
        int size,
        long totalItems,
        int totalPages) {
}
