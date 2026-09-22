package com.glamgest.app.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DurationMinutesValidator implements ConstraintValidator<DurationMinutes, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || value > 0 && value % 15 == 0;
    }
}
