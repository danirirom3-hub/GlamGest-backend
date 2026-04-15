package com.glamgest.app.application.usecase.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;

public interface CreateAppointmentUseCase {

    AppointmentResponseDTO execute(AppointmentRequestDTO appointmentRequestDTO);
}
