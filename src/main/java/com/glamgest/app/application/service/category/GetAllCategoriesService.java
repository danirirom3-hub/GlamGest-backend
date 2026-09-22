package com.glamgest.app.application.service.category;

import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.usecase.category.GetAllCategoriesUseCase;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllCategoriesService implements GetAllCategoriesUseCase {

    private final CategoryRepository categoryRepository;

    public GetAllCategoriesService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponseDTO> execute() {
        return categoryRepository.findAll().stream().map(CreateCategoryService::toResponse).toList();
    }
}
