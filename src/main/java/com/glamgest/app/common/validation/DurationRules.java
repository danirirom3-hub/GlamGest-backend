package com.glamgest.app.common.validation;

import com.glamgest.app.common.exception.InvalidDurationException;

public final class DurationRules {

    private DurationRules() {
    }

    public static Integer validate(Integer durationMinutes) {
        if (durationMinutes == null || durationMinutes <= 0 || durationMinutes % 15 != 0) {
            throw new InvalidDurationException("La duración debe ser un múltiplo de 15 minutos y mayor que cero.");
        }
        return durationMinutes;
    }
}
