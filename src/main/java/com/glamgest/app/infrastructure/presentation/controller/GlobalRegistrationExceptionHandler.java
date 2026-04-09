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

import com.glamgest.app.common.exception.DuplicateEmailException;
import com.glamgest.app.common.exception.ResourceNotFoundException;
import com.glamgest.app.common.exception.RoleNotFoundException;
import com.glamgest.app.infrastructure.presentation.helper.BuilderHelper;

@RestControllerAdvice
public class GlobalRegistrationExceptionHandler {

    @ExceptionHandler({ ResourceNotFoundException.class, RoleNotFoundException.class, NoHandlerFoundException.class })
    ResponseEntity<?> notFound(Exception ex) {
        return this.throwErrorMessage(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DuplicateEmailException.class,
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
        return BuilderHelper.buildResponse(null, ex.getMessage(), httpStatus, false);
    }
}