package com.glamgest.app.common.constant;

import com.glamgest.app.common.exception.InvalidAppointmentStatusException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentStatusRulesTest {

    @Test
    void allowsPendingToConfirmed() {
        assertDoesNotThrow(() -> AppointmentStatusRules.ensureTransition(
                Constant.APPOINTMENT_STATUS_PENDING, Constant.APPOINTMENT_STATUS_CONFIRMED));
    }

    @Test
    void rejectsTransitionFromCompleted() {
        assertThrows(InvalidAppointmentStatusException.class, () -> AppointmentStatusRules.ensureTransition(
                Constant.APPOINTMENT_STATUS_COMPLETED, Constant.APPOINTMENT_STATUS_PENDING));
    }

    @Test
    void rejectsUnknownStatus() {
        assertThrows(InvalidAppointmentStatusException.class, () -> AppointmentStatusRules.validate("UNKNOWN"));
    }
}
