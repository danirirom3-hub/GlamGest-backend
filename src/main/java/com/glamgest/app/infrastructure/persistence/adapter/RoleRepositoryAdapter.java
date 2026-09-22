package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Role;
import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaRoleRepository;
import com.glamgest.app.infrastructure.persistence.entity.Roles;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final JpaRoleRepository jpaRoleRepository;

    public RoleRepositoryAdapter(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public Optional<Role> findById(Integer id) {
        return jpaRoleRepository.findById(id)
                .filter(entity -> entity.getActive() != null && entity.getActive())
                .map(entity -> new Role(entity.getRoleId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRoleRepository.findByName(name)
                .map(entity -> new Role(entity.getRoleId(), entity.getName(), entity.getDescription()));
    }

    @Override
    public boolean existsById(Integer id) {
        return findById(id).isPresent();
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRoleRepository.existsByName(name);
    }

    @Override
    public Role save(Role role) {
        Roles entity = new Roles();
        if (role.getId() != null) {
            entity.setRoleId(role.getId());
        }
        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        entity.setActive(true);

        Roles savedEntity = jpaRoleRepository.save(entity);

        return new Role(savedEntity.getRoleId(), savedEntity.getName(), savedEntity.getDescription());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRoleRepository.softDelete(id);
    }

    @Override
    public List<Role> findAll() {
        return jpaRoleRepository.findAllActive().stream()
                .map(entity -> new Role(entity.getRoleId(), entity.getName(), entity.getDescription()))
                .collect(Collectors.toList());
    }
}
