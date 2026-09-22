package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.dto.category.CategoryUpdateDTO;
import com.glamgest.app.application.usecase.category.CreateCategoryUseCase;
import com.glamgest.app.application.usecase.category.DeleteCategoryUseCase;
import com.glamgest.app.application.usecase.category.GetAllCategoriesUseCase;
import com.glamgest.app.application.usecase.category.UpdateCategoryUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetAllCategoriesUseCase getAllCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase,
                              GetAllCategoriesUseCase getAllCategoriesUseCase,
                              UpdateCategoryUseCase updateCategoryUseCase,
                              DeleteCategoryUseCase deleteCategoryUseCase) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return BuilderHelper.buildResponse(getAllCategoriesUseCase.execute(), "Categories retrieved successfully", HttpStatus.OK, true);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO response = createCategoryUseCase.execute(request);
        return BuilderHelper.buildResponse(response, "Category created successfully", HttpStatus.CREATED, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody CategoryUpdateDTO request) {
        return BuilderHelper.buildResponse(updateCategoryUseCase.execute(id, request), "Category updated successfully", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        deleteCategoryUseCase.execute(id);
        return BuilderHelper.buildResponse(null, "Category deactivated successfully", HttpStatus.NO_CONTENT, true);
    }
}
