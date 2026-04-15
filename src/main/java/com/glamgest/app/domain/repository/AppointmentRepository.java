package com.glamgest.app.domain.repository;

import com.glamgest.app.domain.model.Appointment;

public interface AppointmentRepository {

    Appointment save(Appointment appointment);
}
