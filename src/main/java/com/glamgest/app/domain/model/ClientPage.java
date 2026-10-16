package com.glamgest.app.domain.model;

import java.util.List;

public record ClientPage(List<Client> items, int page, int size, long totalItems, int totalPages) {
}
