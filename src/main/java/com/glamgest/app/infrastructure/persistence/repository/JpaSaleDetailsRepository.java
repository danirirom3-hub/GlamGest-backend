package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.SaleDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;
import java.util.Set;

@Repository
public interface JpaSaleDetailsRepository extends JpaRepository<SaleDetails, Integer> {
    @Query("SELECT DISTINCT detail.appointmentId.appointmentId FROM SaleDetails detail "
            + "WHERE detail.appointmentId.appointmentId IN :appointmentIds AND detail.saleId.status = 'ACTIVE'")
    Set<Integer> findActivelyBilledAppointmentIds(@Param("appointmentIds") Collection<Integer> appointmentIds);

    @Query("SELECT DISTINCT detail.appointmentId.appointmentId, detail.employeeId.employeeId "
            + "FROM SaleDetails detail WHERE detail.appointmentId.appointmentId IN :appointmentIds "
            + "AND detail.saleId.status = 'ACTIVE'")
    List<Object[]> findActiveBillingEmployeesByAppointmentIds(@Param("appointmentIds") Collection<Integer> appointmentIds);

    @Query(value = "SELECT sd.service_id, s.name, COALESCE(SUM(sd.quantity), 0), COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN services s ON s.service_id = sd.service_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.service_id, s.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> revenueByServiceBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT sd.service_id, s.name, COALESCE(c.name, 'Sin categoría'), "
            + "COALESCE(SUM(sd.quantity), 0), COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN services s ON s.service_id = sd.service_id "
            + "LEFT JOIN categories c ON c.category_id = s.category_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.service_id, s.name, c.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> detailedRevenueByServiceBetween(@Param("from") LocalDateTime from,
                                                   @Param("to") LocalDateTime to);

    @Query(value = "SELECT sd.employee_id, e.name, COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN employees e ON e.employee_id = sd.employee_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.employee_id, e.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> revenueByEmployeeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT sd.employee_id, e.name, COUNT(DISTINCT sa.sale_id), "
            + "COALESCE(SUM(sd.subtotal), 0) "
            + "FROM sale_details sd JOIN sales sa ON sa.sale_id = sd.sale_id "
            + "JOIN employees e ON e.employee_id = sd.employee_id "
            + "WHERE sa.status = 'ACTIVE' AND sa.sale_datetime >= :from AND sa.sale_datetime < :to "
            + "GROUP BY sd.employee_id, e.name ORDER BY COALESCE(SUM(sd.subtotal), 0) DESC", nativeQuery = true)
    List<Object[]> detailedRevenueByEmployeeBetween(@Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to);
}
