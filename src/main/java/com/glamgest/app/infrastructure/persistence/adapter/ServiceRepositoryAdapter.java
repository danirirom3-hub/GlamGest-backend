package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.entity.Categories;
import com.glamgest.app.infrastructure.persistence.repository.JpaServiceRepository;
import org.springframework.stereotype.Component;
import com.glamgest.app.common.validation.DurationRules;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServiceRepositoryAdapter implements ServiceRepository {

    private final JpaServiceRepository jpaServiceRepository;

    public ServiceRepositoryAdapter(JpaServiceRepository jpaServiceRepository) {
        this.jpaServiceRepository = jpaServiceRepository;
    }

    @Override
    public Service save(Service service) {
        if (service.getId() == null || service.getDurationMinutes() != null) {
            DurationRules.validate(service.getDurationMinutes());
        }
        Services entity = toEntity(service);
        Services saved = jpaServiceRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Service> findById(Integer id) {
        return jpaServiceRepository.findById(id)
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .map(this::toModel);
    }

    @Override
    public Optional<Service> findByIdIncludingInactive(Integer id) {
        return jpaServiceRepository.findByIdIncludingInactive(id).map(this::toModel);
    }

    @Override
    public List<Service> findAll() {
        return jpaServiceRepository.findAll().stream()
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Service> findAllIncludingInactive() {
        return jpaServiceRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaServiceRepository.softDelete(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaServiceRepository.existsByName(name);
    }

    @Override
    public Optional<Service> findByName(String name) {
        Services entity = jpaServiceRepository.findByName(name);
        return entity != null ? Optional.of(toModel(entity)) : Optional.empty();
    }

    @Override
    public Optional<Service> findByNameIncludingInactive(String name) {
        return jpaServiceRepository.findByNameIncludingInactive(name).map(this::toModel);
    }

    private Services toEntity(Service model) {
        Services entity = new Services();
        entity.setServiceId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setPrice(model.getPrice());
        entity.setDurationMinutes(model.getDurationMinutes());
        entity.setActive(model.getActive());
        if (model.getCategoryId() != null) {
            entity.setCategoryId(new Categories(model.getCategoryId()));
        }
        return entity;
    }

    private Service toModel(Services entity) {
        return new Service(
                entity.getServiceId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getDurationMinutes(),
                entity.getActive(),
                entity.getCategoryId() != null ? entity.getCategoryId().getCategoryId() : null,
                entity.getCategoryId() != null ? entity.getCategoryId().getName() : null
        );
    }
}
