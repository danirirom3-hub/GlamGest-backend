package com.glamgest.app.application.service.category;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.dto.category.CategoryUpdateDTO;
import com.glamgest.app.common.exception.DuplicateCategoryNameException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.domain.model.Category;
import com.glamgest.app.domain.repository.CategoryRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import java.util.Optional;

class CategoryServiceTest {

    @Test
    void createsCategoryWhenNameIsUnique() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CreateCategoryService service = new CreateCategoryService(repository);
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Masajes");
        request.setDescription("Servicios de masaje");

        when(repository.existsByName("Masajes")).thenReturn(false);
        when(repository.findByNameIncludingInactive("Masajes")).thenReturn(Optional.empty());
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

    @Test
    void reactivatesInactiveCategoryWhenCreatingSameName() {
        CategoryRepository repository = mock(CategoryRepository.class);
        CreateCategoryService service = new CreateCategoryService(repository);
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Masajes");
        request.setDescription("Actualizada");
        Category inactive = new Category(4, "Masajes", "Anterior", false);

        when(repository.existsByName("Masajes")).thenReturn(false);
        when(repository.findByNameIncludingInactive("Masajes")).thenReturn(Optional.of(inactive));
        when(repository.save(inactive)).thenReturn(inactive);

        CategoryResponseDTO response = service.execute(request);

        assertEquals(4, response.id());
        assertEquals(true, response.active());
        assertEquals("Actualizada", response.description());
    }

    @Test
    void updatesInactiveCategoryAndCanReactivateIt() {
        CategoryRepository repository = mock(CategoryRepository.class);
        UpdateCategoryService service = new UpdateCategoryService(repository);
        CategoryUpdateDTO request = new CategoryUpdateDTO();
        request.setActive(true);
        Category inactive = new Category(5, "Uñas", "Anterior", false);

        when(repository.findByIdIncludingInactive(5)).thenReturn(Optional.of(inactive));
        when(repository.save(inactive)).thenReturn(inactive);

        CategoryResponseDTO response = service.execute(5, request);

        assertEquals(5, response.id());
        assertEquals(true, response.active());
    }

    @Test
    void refusesToDeactivateCategoryWithRelatedServices() {
        CategoryRepository repository = mock(CategoryRepository.class);
        DeleteCategoryService service = new DeleteCategoryService(repository);

        when(repository.findById(6)).thenReturn(Optional.of(new Category(6, "Cabello", null, true)));
        when(repository.hasServices(6)).thenReturn(true);

        assertThrows(OperationNotAllowedException.class, () -> service.execute(6));
        verify(repository, org.mockito.Mockito.never()).deleteById(6);
    }
}
