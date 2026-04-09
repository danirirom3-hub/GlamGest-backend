package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findById(Integer id);

    Optional<Role> findByName(String name);

    boolean existsById(Integer id);

    boolean existsByName(String name);

    Role save(Role role);

    void deleteById(Integer id);

    List<Role> findAll();
}
