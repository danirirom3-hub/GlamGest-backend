package com.glamgest.app.application.service.category;

import com.glamgest.app.application.usecase.category.DeleteCategoryUseCase;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteCategoryService implements DeleteCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public DeleteCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public String execute(Integer id) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        if (categoryRepository.hasServices(id)) {
            throw new OperationNotAllowedException(
                    "No se puede desactivar una categoría que tiene servicios relacionados. "
                            + "Retira la categoría de esos servicios primero.");
        }
        categoryRepository.deleteById(id);
        return category.getName();
    }
}
