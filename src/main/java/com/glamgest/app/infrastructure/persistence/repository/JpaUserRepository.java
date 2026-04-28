package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<Users, Integer> {

    @Query("SELECT u FROM Users u WHERE u.email = :email AND u.active = true")
    Optional<Users> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u WHERE u.email = :email AND u.active = true")
    boolean existsByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.active = false WHERE u.userId = :id")
    void softDelete(@Param("id") Integer id);
}
