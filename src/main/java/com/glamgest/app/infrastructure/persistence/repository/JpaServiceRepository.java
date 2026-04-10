package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaServiceRepository extends JpaRepository<Services, Integer> {

    @Query("SELECT COUNT(s) > 0 FROM Services s WHERE s.name = :name")
    boolean existsByName(@Param("name") String name);

    @Query("SELECT s FROM Services s WHERE s.name = :name")
    Services findByName(@Param("name") String name);
}