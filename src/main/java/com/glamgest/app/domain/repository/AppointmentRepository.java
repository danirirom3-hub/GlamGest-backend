package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository {

    Optional<Appointment> findById(Integer id);

    Appointment save(Appointment appointment);

    void deleteById(Integer id);

    List<Appointment> findAll();
}
