package com.glamgest.app.application.service.category;

import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.dto.category.CategoryUpdateDTO;
import com.glamgest.app.application.usecase.category.UpdateCategoryUseCase;
import com.glamgest.app.common.exception.DuplicateCategoryNameException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.domain.model.Category;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateCategoryService implements UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public UpdateCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDTO execute(Integer id, CategoryUpdateDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        if (request.getName() != null && !request.getName().equalsIgnoreCase(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new DuplicateCategoryNameException("Category name already exists: " + request.getName());
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        return CreateCategoryService.toResponse(categoryRepository.save(category));
    }
}
