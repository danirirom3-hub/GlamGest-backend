package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByName(String name);

    boolean existsByName(String name);
}
