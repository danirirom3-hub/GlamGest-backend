package com.glamgest.app.infrastructure.persistence.repository;

import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaAppointmentRepository extends JpaRepository<Appointments, Integer> {
    List<Appointments> findByClientId_ClientId(Integer clientId);
}
