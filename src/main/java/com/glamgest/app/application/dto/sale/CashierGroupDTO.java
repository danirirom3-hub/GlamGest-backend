package com.glamgest.app.application.dto.sale;

import java.time.LocalDate;
import java.util.List;

public record CashierGroupDTO(
        Integer clientId,
        String clientName,
        LocalDate businessDate,
        List<CashierAppointmentDTO> appointments) {
}
