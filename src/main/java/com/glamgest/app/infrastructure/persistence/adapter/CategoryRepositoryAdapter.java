package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Category;
import com.glamgest.app.domain.repository.CategoryRepository;
import com.glamgest.app.infrastructure.persistence.entity.Categories;
import com.glamgest.app.infrastructure.persistence.repository.JpaCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final JpaCategoryRepository jpaRepository;

    public CategoryRepositoryAdapter(JpaCategoryRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Category save(Category category) {
        Categories entity = new Categories();
        entity.setCategoryId(category.getId());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        entity.setActive(category.getActive() == null ? true : category.getActive());
        return toModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<Category> findById(Integer id) {
        return jpaRepository.findActiveById(id).map(this::toModel);
    }

    @Override
    public List<Category> findAll() {
        return jpaRepository.findAllActive().stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.softDelete(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsActiveByName(name);
    }

    private Category toModel(Categories entity) {
        return new Category(entity.getCategoryId(), entity.getName(), entity.getDescription(), entity.getActive());
    }
}
