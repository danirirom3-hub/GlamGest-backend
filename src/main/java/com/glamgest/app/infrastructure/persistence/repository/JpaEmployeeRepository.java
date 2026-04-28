package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface JpaEmployeeRepository extends JpaRepository<Employees, Integer> {

    @Query("SELECT e FROM Employees e WHERE e.phone = :phone AND e.active = true")
    Optional<Employees> findByPhone(@Param("phone") String phone);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Employees e WHERE e.phone = :phone AND e.active = true")
    boolean existsByPhone(@Param("phone") String phone);

    @Modifying
    @Transactional
    @Query("UPDATE Employees e SET e.active = false WHERE e.employeeId = :id")
    void softDelete(@Param("id") Integer id);
}