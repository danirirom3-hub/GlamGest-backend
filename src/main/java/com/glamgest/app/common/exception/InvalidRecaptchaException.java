package com.glamgest.app.common.exception;

public class InvalidRecaptchaException extends RuntimeException {

    public InvalidRecaptchaException(String message) {
        super(message);
    }

    public InvalidRecaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}
