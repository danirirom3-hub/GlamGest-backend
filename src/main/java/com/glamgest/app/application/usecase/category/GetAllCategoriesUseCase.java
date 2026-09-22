package com.glamgest.app.application.usecase.category;

import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import java.util.List;

public interface GetAllCategoriesUseCase {
    List<CategoryResponseDTO> execute();
}
