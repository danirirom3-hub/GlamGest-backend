package com.glamgest.app.application.usecase.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;

public interface ChangeAppointmentStatusUseCase {

    AppointmentResponseDTO execute(Integer id, String status);
}
