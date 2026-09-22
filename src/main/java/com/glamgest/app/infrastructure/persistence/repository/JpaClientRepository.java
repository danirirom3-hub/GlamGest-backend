package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Clients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface JpaClientRepository extends JpaRepository<Clients, Integer> {

    @Query("SELECT c FROM Clients c WHERE c.email = :email AND c.active = true")
    Optional<Clients> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM Clients c WHERE c.phone = :phone AND c.active = true")
    Optional<Clients> findByPhone(@Param("phone") String phone);

    @Query("SELECT c FROM Clients c WHERE c.userId.userId = :userId AND c.active = true")
    Optional<Clients> findByUserId_UserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(c) > 0 FROM Clients c WHERE c.email = :email AND c.active = true")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(c) > 0 FROM Clients c WHERE c.phone = :phone AND c.active = true")
    boolean existsByPhone(@Param("phone") String phone);

    @Query("SELECT c FROM Clients c WHERE c.active = true")
    java.util.List<Clients> findAllActive();

    @Modifying
    @Transactional
    @Query("UPDATE Clients c SET c.active = false WHERE c.clientId = :id")
    void softDelete(@Param("id") Integer id);
}
