package com.glamgest.app.application.usecase.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;

import java.util.List;

public interface GetAllAppointmentsUseCase {

    List<AppointmentResponseDTO> execute();
}
