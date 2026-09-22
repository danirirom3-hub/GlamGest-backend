package com.glamgest.app.common.constant;

import com.glamgest.app.common.exception.InvalidAppointmentStatusException;

import java.util.Map;
import java.util.Set;

public final class AppointmentStatusRules {

    private static final Set<String> FINAL_STATUSES = Set.of(
            Constant.APPOINTMENT_STATUS_COMPLETED,
            Constant.APPOINTMENT_STATUS_CANCELLED,
            Constant.APPOINTMENT_STATUS_NO_SHOW);
    private static final Set<String> VALID_STATUSES = Set.of(
            Constant.APPOINTMENT_STATUS_PENDING,
            Constant.APPOINTMENT_STATUS_CONFIRMED,
            Constant.APPOINTMENT_STATUS_COMPLETED,
            Constant.APPOINTMENT_STATUS_CANCELLED,
            Constant.APPOINTMENT_STATUS_NO_SHOW);
    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            Constant.APPOINTMENT_STATUS_PENDING, Set.of(
                    Constant.APPOINTMENT_STATUS_CONFIRMED,
                    Constant.APPOINTMENT_STATUS_CANCELLED),
            Constant.APPOINTMENT_STATUS_CONFIRMED, Set.of(
                    Constant.APPOINTMENT_STATUS_COMPLETED,
                    Constant.APPOINTMENT_STATUS_CANCELLED,
                    Constant.APPOINTMENT_STATUS_NO_SHOW));

    private AppointmentStatusRules() {
    }

    public static String normalize(String status) {
        return status == null ? null : status.trim().toUpperCase();
    }

    public static String validate(String status) {
        String normalized = normalize(status);
        if (!VALID_STATUSES.contains(normalized)) {
            throw new InvalidAppointmentStatusException("Estado de cita no válido: " + status);
        }
        return normalized;
    }

    public static void ensureTransition(String currentStatus, String targetStatus) {
        String current = normalize(currentStatus == null ? Constant.APPOINTMENT_STATUS_PENDING : currentStatus);
        String target = validate(targetStatus);
        if (current.equals(target)) {
            return;
        }
        if (FINAL_STATUSES.contains(current)
                || !ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(target)) {
            throw new InvalidAppointmentStatusException(
                    "No se puede cambiar una cita de " + current + " a " + target);
        }
    }
}
