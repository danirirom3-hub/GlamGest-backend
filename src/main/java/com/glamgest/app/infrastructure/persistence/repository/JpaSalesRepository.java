package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaSalesRepository extends JpaRepository<Sales, Integer> {
    @Query("SELECT COUNT(s) FROM Sales s WHERE s.status = 'ACTIVE' AND s.saleDatetime >= :from AND s.saleDatetime < :to")
    long countActiveBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COALESCE(SUM(s.total), 0) FROM Sales s WHERE s.status = 'ACTIVE' AND s.saleDatetime >= :from AND s.saleDatetime < :to")
    long sumActiveBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT DATE(sale_datetime), COUNT(*), COALESCE(SUM(total), 0) "
            + "FROM sales WHERE status = 'ACTIVE' AND sale_datetime >= :from AND sale_datetime < :to "
            + "GROUP BY DATE(sale_datetime) ORDER BY DATE(sale_datetime)", nativeQuery = true)
    List<Object[]> revenueByDay(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT sa.sale_datetime, sa.sale_id, c.name, e.name, s.name, "
            + "sd.quantity, sd.unit_price, sd.subtotal, sa.payment_type "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN clients c ON c.client_id = sa.client_id "
            + "JOIN employees e ON e.employee_id = sd.employee_id "
            + "JOIN services s ON s.service_id = sd.service_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "ORDER BY sa.sale_datetime, sa.sale_id, sd.detail_id", nativeQuery = true)
    List<Object[]> activeDetailBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Modifying
    @Transactional
    @Query("UPDATE Sales s SET s.status = 'VOIDED', s.voidedAt = CURRENT_TIMESTAMP WHERE s.saleId = :id AND s.status <> 'VOIDED'")
    int voidSale(@Param("id") Integer id);
}
