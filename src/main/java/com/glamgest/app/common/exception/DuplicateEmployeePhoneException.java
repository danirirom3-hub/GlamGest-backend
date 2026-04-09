package com.glamgest.app.common.exception;

public class DuplicateEmployeePhoneException extends RuntimeException {

    public DuplicateEmployeePhoneException(String message) {
        super(message);
    }
}