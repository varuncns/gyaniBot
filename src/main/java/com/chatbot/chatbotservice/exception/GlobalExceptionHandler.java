package com.chatbot.chatbotservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidApiKeyException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidApiKey(InvalidApiKeyException ex) {
        return new ResponseEntity<>(
                Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Unauthorized",
                        "message", ex.getMessage()
                ),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return new ResponseEntity<>(
                Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", "Something went wrong. Please try again later."
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
