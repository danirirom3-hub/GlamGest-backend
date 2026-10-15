package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);
    Optional<Category> findById(Integer id);
    Optional<Category> findByIdIncludingInactive(Integer id);
    List<Category> findAll();
    List<Category> findAllIncludingInactive();
    void deleteById(Integer id);
    boolean hasServices(Integer id);
    boolean existsByName(String name);
    Optional<Category> findByNameIncludingInactive(String name);
}
