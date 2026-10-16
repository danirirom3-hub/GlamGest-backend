package com.glamgest.app.application.dto.client;

import java.util.List;

public record ClientPageResponseDTO(
        List<ClientResponseDTO> items,
        int page,
        int size,
        long totalItems,
        int totalPages) {
}
