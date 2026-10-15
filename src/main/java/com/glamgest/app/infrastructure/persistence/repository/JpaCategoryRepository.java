package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCategoryRepository extends JpaRepository<Categories, Integer> {

    @Query("SELECT c FROM Categories c WHERE c.categoryId = :id AND c.active = true")
    Optional<Categories> findActiveById(@Param("id") Integer id);

    @Query("SELECT c FROM Categories c WHERE c.categoryId = :id")
    Optional<Categories> findByIdIncludingInactive(@Param("id") Integer id);

    @Query("SELECT c FROM Categories c WHERE c.active = true ORDER BY c.name")
    List<Categories> findAllActive();

    @Query("SELECT c FROM Categories c ORDER BY c.name")
    List<Categories> findAllIncludingInactive();

    @Query("SELECT COUNT(c) > 0 FROM Categories c WHERE LOWER(c.name) = LOWER(:name) AND c.active = true")
    boolean existsActiveByName(@Param("name") String name);

    @Query("SELECT c FROM Categories c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Categories> findByNameIncludingInactive(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE Categories c SET c.active = false WHERE c.categoryId = :id")
    void softDelete(@Param("id") Integer id);

    @Query("SELECT COUNT(s) > 0 FROM Services s WHERE s.categoryId.categoryId = :id")
    boolean hasServices(@Param("id") Integer id);
}
