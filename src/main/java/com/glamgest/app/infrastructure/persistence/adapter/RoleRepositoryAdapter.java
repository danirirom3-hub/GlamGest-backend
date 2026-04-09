package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.repository.RoleRepository;
import com.glamgest.app.infrastructure.persistence.repository.JpaRoleRepository;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepositoryAdapter implements RoleRepository {

    private final JpaRoleRepository jpaRoleRepository;

    public RoleRepositoryAdapter(JpaRoleRepository jpaRoleRepository) {
        this.jpaRoleRepository = jpaRoleRepository;
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRoleRepository.existsById(id);
    }
}
