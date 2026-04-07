package com.glamgest.app.infrastructure.presentation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.glamgest.app.common.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalRegistrationExceptionHandler {

    @ExceptionHandler({ ResourceNotFoundException.class, NoHandlerFoundException.class })
    ResponseEntity<?> notFound(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, })
    ResponseEntity<?> throwBadRequest(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ RuntimeException.class, Exception.class })
    ResponseEntity<?> throwUnknownError(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<?> throwErrorMessage(Exception ex, HttpStatus httpStatus) {
        return buildResponse(null, ex.getMessage(), httpStatus, false);
    }

    public static ResponseEntity<?> buildResponse(Object data, String message, HttpStatus status, boolean successful) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("status", status.value());
        response.put("successful", successful);
        response.put(successful ? "message" : "error", message);
        return ResponseEntity.status(status).body(response);
    }

}