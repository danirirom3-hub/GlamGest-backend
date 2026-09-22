package com.glamgest.app.common.validation;

import com.glamgest.app.application.dto.appointment.AppointmentRequestDTO;
import com.glamgest.app.application.dto.service.ServiceRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurationValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsQuarterHourDurations() {
        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setName("Masaje");
        request.setPrice(100);
        request.setDurationMinutes(45);

        assertTrue(validator.validateProperty(request, "durationMinutes").isEmpty());
    }

    @Test
    void rejectsNonQuarterHourServiceDuration() {
        ServiceRequestDTO request = new ServiceRequestDTO();
        request.setDurationMinutes(37);

        assertFalse(validator.validateProperty(request, "durationMinutes").isEmpty());
    }

    @Test
    void allowsOmittedAppointmentDurationForServiceDefault() {
        AppointmentRequestDTO request = new AppointmentRequestDTO();

        assertTrue(validator.validateProperty(request, "durationMinutes").isEmpty());
    }
}
