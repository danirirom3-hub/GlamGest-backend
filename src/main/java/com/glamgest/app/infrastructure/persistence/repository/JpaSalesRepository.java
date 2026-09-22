package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaSalesRepository extends JpaRepository<Sales, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE Sales s SET s.status = 'VOIDED', s.voidedAt = CURRENT_TIMESTAMP WHERE s.saleId = :id AND s.status <> 'VOIDED'")
    int voidSale(@Param("id") Integer id);
}
