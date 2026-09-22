package com.glamgest.app.application.usecase.category;

import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.dto.category.CategoryUpdateDTO;

public interface UpdateCategoryUseCase {
    CategoryResponseDTO execute(Integer id, CategoryUpdateDTO request);
}
