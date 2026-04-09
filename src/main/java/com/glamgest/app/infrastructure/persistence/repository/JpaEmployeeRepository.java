package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaEmployeeRepository extends JpaRepository<Employees, Integer> {

    Optional<Employees> findByPhone(String phone);

    boolean existsByPhone(String phone);
}