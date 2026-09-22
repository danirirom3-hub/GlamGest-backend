package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaSaleDetailsRepository extends JpaRepository<SaleDetails, Integer> {
    @Query(value = "SELECT sd.service_id, s.name, COALESCE(SUM(sd.quantity), 0), COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN services s ON s.service_id = sd.service_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.service_id, s.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> revenueByServiceBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT sd.employee_id, e.name, COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN employees e ON e.employee_id = sd.employee_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.employee_id, e.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> revenueByEmployeeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
