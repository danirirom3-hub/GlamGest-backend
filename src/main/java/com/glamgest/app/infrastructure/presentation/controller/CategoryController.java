package com.glamgest.app.infrastructure.presentation.controller;

import com.glamgest.app.application.dto.category.CategoryRequestDTO;
import com.glamgest.app.application.dto.category.CategoryResponseDTO;
import com.glamgest.app.application.dto.category.CategoryUpdateDTO;
import com.glamgest.app.application.usecase.category.CreateCategoryUseCase;
import com.glamgest.app.application.usecase.category.DeleteCategoryUseCase;
import com.glamgest.app.application.usecase.category.GetAllCategoriesUseCase;
import com.glamgest.app.application.usecase.category.UpdateCategoryUseCase;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;
import com.glamgest.app.infrastructure.configuration.AuditLogContext;
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
    private final AuditLogContext auditLogContext;

    public CategoryController(CreateCategoryUseCase createCategoryUseCase,
                              GetAllCategoriesUseCase getAllCategoriesUseCase,
                              UpdateCategoryUseCase updateCategoryUseCase,
                              DeleteCategoryUseCase deleteCategoryUseCase,
                              AuditLogContext auditLogContext) {
        this.createCategoryUseCase = createCategoryUseCase;
        this.getAllCategoriesUseCase = getAllCategoriesUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.auditLogContext = auditLogContext;
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return BuilderHelper.buildResponse(getAllCategoriesUseCase.execute(), "Categories retrieved successfully", HttpStatus.OK, true);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAllForAdmin() {
        return BuilderHelper.buildResponse(getAllCategoriesUseCase.executeIncludingInactive(),
                "Categories retrieved successfully", HttpStatus.OK, true);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO response = createCategoryUseCase.execute(request);
        auditLogContext.setResource(response.id().toString(), response.name());
        return BuilderHelper.buildResponse(response, "Category created successfully", HttpStatus.CREATED, true);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody CategoryUpdateDTO request) {
        CategoryResponseDTO response = updateCategoryUseCase.execute(id, request);
        auditLogContext.setResource(response.id().toString(), response.name());
        return BuilderHelper.buildResponse(response, "Category updated successfully", HttpStatus.OK, true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        String categoryName = deleteCategoryUseCase.execute(id);
        auditLogContext.setResource(id.toString(), categoryName);
        return BuilderHelper.buildResponse(null, "Category deactivated successfully", HttpStatus.NO_CONTENT, true);
    }
}
