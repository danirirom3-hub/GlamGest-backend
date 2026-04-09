package com.glamgest.app.infrastructure.presentation.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BuilderHelper {

    public static ResponseEntity<?> buildResponse(Object data, String message, HttpStatus status, boolean successful) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("status", status.value());
        response.put("successful", successful);
        response.put(successful ? "message" : "error", message);
        return ResponseEntity.status(status).body(response);
    }
}
