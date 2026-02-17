package com.weatherinfopincode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- VALIDATION ----------
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class   // ⭐ IMPORTANT (date parsing)
    })
    public ResponseEntity<?> handleBadRequest(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Request",
                "Invalid or missing request parameters");
    }

    // ---------- FUTURE DATE ----------
    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<?> handleInvalidDate(InvalidDateException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid Date", ex.getMessage());
    }

    // ---------- PINCODE NOT FOUND ----------
    @ExceptionHandler(PincodeNotFoundException.class)
    public ResponseEntity<?> handlePincode(PincodeNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Pincode Not Found", ex.getMessage());
    }

    // ---------- WEATHER API FAILURE ----------
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleApiError(HttpClientErrorException ex) {
        return build(HttpStatus.BAD_GATEWAY, "Weather API Error",
                "Unable to fetch weather data. Please try again later.");
    }

    // ---------- FALLBACK ----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralError(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("status", status.value());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, status);
    }
}
