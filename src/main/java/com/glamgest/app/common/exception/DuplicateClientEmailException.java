package com.glamgest.app.common.exception;

public class DuplicateClientEmailException extends RuntimeException {

    public DuplicateClientEmailException(String message) {
        super(message);
    }
}