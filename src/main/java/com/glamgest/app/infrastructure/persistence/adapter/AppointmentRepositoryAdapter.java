package com.glamgest.app.infrastructure.persistence.adapter;

import com.glamgest.app.domain.model.Appointment;
import com.glamgest.app.domain.repository.AppointmentRepository;
import com.glamgest.app.infrastructure.persistence.entity.Appointments;
import com.glamgest.app.infrastructure.persistence.entity.Clients;
import com.glamgest.app.infrastructure.persistence.entity.Employees;
import com.glamgest.app.infrastructure.persistence.entity.Services;
import com.glamgest.app.infrastructure.persistence.entity.Users;
import com.glamgest.app.infrastructure.persistence.repository.JpaAppointmentRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepositoryAdapter implements AppointmentRepository {

    private final JpaAppointmentRepository jpaAppointmentRepository;

    public AppointmentRepositoryAdapter(JpaAppointmentRepository jpaAppointmentRepository) {
        this.jpaAppointmentRepository = jpaAppointmentRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        Appointments entity = toEntity(appointment);
        Appointments saved = jpaAppointmentRepository.save(entity);
        return toModel(saved);
    }

    private Appointments toEntity(Appointment appointment) {
        Appointments entity = new Appointments();
        if (appointment.getId() != null) {
            entity.setAppointmentId(appointment.getId());
        }
        entity.setAppointmentDatetime(appointment.getAppointmentDatetime());
        entity.setStatus(appointment.getStatus());
        entity.setNotes(appointment.getNotes());

        if (appointment.getClientId() != null) {
            entity.setClientId(new Clients(appointment.getClientId()));
        }
        if (appointment.getEmployeeId() != null) {
            entity.setEmployeeId(new Employees(appointment.getEmployeeId()));
        }
        if (appointment.getServiceId() != null) {
            entity.setServiceId(new Services(appointment.getServiceId()));
        }
        if (appointment.getUserId() != null) {
            entity.setUserId(new Users(appointment.getUserId()));
        }

        return entity;
    }

    private Appointment toModel(Appointments entity) {
        Appointment appointment = new Appointment();
        appointment.setId(entity.getAppointmentId());
        appointment.setAppointmentDatetime(entity.getAppointmentDatetime());
        appointment.setStatus(entity.getStatus());
        appointment.setNotes(entity.getNotes());
        appointment.setClientId(entity.getClientId() != null ? entity.getClientId().getClientId() : null);
        appointment.setEmployeeId(entity.getEmployeeId() != null ? entity.getEmployeeId().getEmployeeId() : null);
        appointment.setServiceId(entity.getServiceId() != null ? entity.getServiceId().getServiceId() : null);
        appointment.setUserId(entity.getUserId() != null ? entity.getUserId().getUserId() : null);
        return appointment;
    }
}
