package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaClientRepository extends JpaRepository<Clients, Integer> {

    Optional<Clients> findByEmail(String email);

    Optional<Clients> findByPhone(String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}