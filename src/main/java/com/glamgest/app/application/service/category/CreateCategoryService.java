package com.glamgest.app.application.service.category;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.usecase.category.CreateCategoryUseCase;
import com.glamgest.app.common.exception.DuplicateCategoryNameException;
import com.glamgest.app.domain.model.Category;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCategoryService implements CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CreateCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponseDTO execute(CategoryRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateCategoryNameException("Category name already exists: " + request.getName());
        }
        var inactiveCategory = categoryRepository.findByNameIncludingInactive(request.getName())
                .filter(category -> !Boolean.TRUE.equals(category.getActive()));
        if (inactiveCategory.isPresent()) {
            Category category = inactiveCategory.get();
            category.setActive(true);
            category.setDescription(request.getDescription());
            return toResponse(categoryRepository.save(category));
        }
        Category category = new Category(null, request.getName(), request.getDescription(), true);
        return toResponse(categoryRepository.save(category));
    }

    static CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName(), category.getDescription(), category.getActive());
    }
}
