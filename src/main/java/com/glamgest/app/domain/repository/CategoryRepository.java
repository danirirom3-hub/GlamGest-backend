package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);
    Optional<Category> findById(Integer id);
    List<Category> findAll();
    void deleteById(Integer id);
    boolean existsByName(String name);
}
