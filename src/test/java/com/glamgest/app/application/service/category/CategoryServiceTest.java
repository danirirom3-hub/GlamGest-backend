package com.glamgest.app.application.service.category;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.common.exception.DuplicateCategoryNameException;
import com.glamgest.app.domain.model.Category;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    @Test
    void createsCategoryWhenNameIsUnique() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CreateCategoryService service = new CreateCategoryService(repository);
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Masajes");
        request.setDescription("Servicios de masaje");

        when(repository.existsByName("Masajes")).thenReturn(false);
        when(repository.save(any(Category.class)))
                .thenReturn(new Category(1, "Masajes", "Servicios de masaje", true));

        CategoryResponseDTO response = service.execute(request);

        assertEquals(1, response.id());
        assertEquals("Masajes", response.name());
        assertEquals(true, response.active());
    }

    @Test
    void rejectsDuplicateCategoryName() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CreateCategoryService service = new CreateCategoryService(repository);
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Masajes");

        when(repository.existsByName("Masajes")).thenReturn(true);

        assertThrows(DuplicateCategoryNameException.class, () -> service.execute(request));
    }
}
