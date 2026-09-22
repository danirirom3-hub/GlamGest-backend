package com.glamgest.app.application.usecase.category;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;

public interface CreateCategoryUseCase {
    CategoryResponseDTO execute(CategoryRequestDTO request);
}
