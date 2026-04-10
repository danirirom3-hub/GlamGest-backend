package com.glamgest.app.common.exception;

public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(String message) {
        super(message);
    }
}