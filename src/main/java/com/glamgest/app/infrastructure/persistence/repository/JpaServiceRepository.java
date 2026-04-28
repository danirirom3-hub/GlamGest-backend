package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaServiceRepository extends JpaRepository<Services, Integer> {

    @Query("SELECT COUNT(s) > 0 FROM Services s WHERE s.name = :name AND s.active = true")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT s FROM Services s WHERE s.name = :name AND s.active = true")
    Services findByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE Services s SET s.active = false WHERE s.serviceId = :id")
    void softDelete(@Param("id") Integer id);
}