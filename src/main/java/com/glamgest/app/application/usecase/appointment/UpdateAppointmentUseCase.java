package com.glamgest.app.application.usecase.appointment;

import com.glamgest.app.application.dto.appointment.AppointmentResponseDTO;
import com.glamgest.app.application.dto.appointment.AppointmentUpdateDTO;

public interface UpdateAppointmentUseCase {

    AppointmentResponseDTO execute(AppointmentUpdateDTO appointmentUpdateDTO);
}
