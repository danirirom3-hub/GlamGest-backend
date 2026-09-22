package com.glamgest.app.infrastructure.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;

import com.glamgest.app.common.exception.DuplicateClientEmailException;
import com.glamgest.app.common.exception.DuplicateClientPhoneException;
import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.DuplicateEmployeePhoneException;
import com.glamgest.app.common.exception.DuplicateRoleNameException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.common.exception.OperationNotAllowedException;
import com.glamgest.app.common.exception.InvalidAppointmentStatusException;
import com.glamgest.app.common.exception.InvalidDashboardPeriodException;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;

@RestControllerAdvice
public class GlobalRegistrationExceptionHandler {

    @ExceptionHandler({ ResourceNotFoundException.class, RoleNotFoundException.class, NoHandlerFoundException.class })
    ResponseEntity<?> notFound(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DuplicateEmailException.class,
            DuplicateEmployeePhoneException.class,
            DuplicateClientEmailException.class,
            DuplicateClientPhoneException.class,
            DuplicateRoleNameException.class,
            OperationNotAllowedException.class,
            InvalidAppointmentStatusException.class,
            InvalidDashboardPeriodException.class,
            HttpMessageNotReadableException.class,
             MethodArgumentTypeMismatchException.class })
    ResponseEntity<?> throwBadRequest(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<?> unauthorized(Exception ex) {
        return this.throwErrorMessage(new Exception("Credenciales inválidas"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        var errors = new java.util.LinkedHashMap<String, String>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return BuilderHelper.buildResponse(errors, "Datos inválidos", HttpStatus.BAD_REQUEST, false);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<?> forbidden(Exception ex) {
        return this.throwErrorMessage(new Exception("No tiene permisos para realizar esta operación"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ RuntimeException.class, Exception.class })
    ResponseEntity<?> throwUnknownError(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> throwErrorMessage(Exception ex, HttpStatus httpStatus) {
        return BuilderHelper.buildResponse(null, ex.getMessage(), httpStatus, false);
    }
}
