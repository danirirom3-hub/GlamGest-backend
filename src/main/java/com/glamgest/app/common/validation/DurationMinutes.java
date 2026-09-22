package com.glamgest.app.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DurationMinutesValidator.class)
@Target({FIELD, METHOD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface DurationMinutes {

    String message() default "La duración debe ser un múltiplo de 15 minutos y mayor que cero.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
