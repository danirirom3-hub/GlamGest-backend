package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface JpaAppointmentRepository extends JpaRepository<Appointments, Integer> {
    List<Appointments> findByClientId_ClientId(Integer clientId);

    List<Appointments> findByEmployeeId_EmployeeId(Integer employeeId);

    @Query("SELECT COUNT(a) FROM Appointments a WHERE a.appointmentDatetime >= :from AND a.appointmentDatetime < :to")
    long countBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT a.status, COUNT(a) FROM Appointments a WHERE a.appointmentDatetime >= :from AND a.appointmentDatetime < :to GROUP BY a.status")
    List<Object[]> countByStatusBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT a.employee_id, e.name, COUNT(a.appointment_id) "
            + "FROM appointments a JOIN employees e ON e.employee_id = a.employee_id "
            + "WHERE a.appointment_datetime >= :from AND a.appointment_datetime < :to "
            + "GROUP BY a.employee_id, e.name ORDER BY COUNT(a.appointment_id) DESC", nativeQuery = true)
    List<Object[]> countByEmployeeBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
