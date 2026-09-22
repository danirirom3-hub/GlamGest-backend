package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaRoleRepository extends JpaRepository<Roles, Integer> {

    @Query("SELECT r FROM Roles r WHERE r.name = :name AND r.active = true")
    Optional<Roles> findByName(@Param("name") String name);

    @Query("SELECT COUNT(r) > 0 FROM Roles r WHERE r.name = :name AND r.active = true")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT r FROM Roles r WHERE r.active = true")
    java.util.List<Roles> findAllActive();

    @Modifying
    @Transactional
    @Query("UPDATE Roles r SET r.active = false WHERE r.roleId = :id")
    void softDelete(@Param("id") Integer id);
}
