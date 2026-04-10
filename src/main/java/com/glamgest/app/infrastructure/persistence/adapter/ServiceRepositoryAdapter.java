package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Service;
import com.glamgest.app.domain.repository.ServiceRepository;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.repository.JpaServiceRepository;
import org.springframework.stereotype.Component;

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
        Services entity = toEntity(service);
        Services saved = jpaServiceRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public Optional<Service> findById(Integer id) {
        return jpaServiceRepository.findById(id).map(this::toModel);
    }

    @Override
    public List<Service> findAll() {
        return jpaServiceRepository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaServiceRepository.deleteById(id);
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

    private Services toEntity(Service model) {
        Services entity = new Services();
        entity.setServiceId(model.getId());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        entity.setPrice(model.getPrice());
        entity.setDurationMinutes(model.getDurationMinutes());
        entity.setActive(model.getActive());
        return entity;
    }

    private Service toModel(Services entity) {
        return new Service(
                entity.getServiceId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getDurationMinutes(),
                entity.getActive()
        );
    }
}
