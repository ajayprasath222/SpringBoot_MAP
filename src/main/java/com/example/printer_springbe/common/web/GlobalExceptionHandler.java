package com.example.printer_springbe.common.web;

import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ApiResponse;
import com.example.printer_springbe.common.response.ApiResponses;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Maps framework and validation errors into the standard {@link ApiResponse} envelope.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusiness(BusinessException ex) {
        return ApiResponses.entity(ex.httpStatus(), ex.responseCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(err -> Map.of(
                        "field", err.getField(),
                        "message", err.getDefaultMessage() == null ? "" : err.getDefaultMessage()
                ))
                .toList());
        return ApiResponses.entity(HttpStatus.BAD_REQUEST, ResponseCode.VALIDATION_FAILED, message, data);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResponse> handleMissingHeader(MissingRequestHeaderException ex) {
        return ApiResponses.entity(
                HttpStatus.BAD_REQUEST,
                ResponseCode.BAD_REQUEST,
                "Missing required header: " + ex.getHeaderName()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponses.entity(HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST, "Malformed request body");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse> handleNoResource(NoResourceFoundException ex) {
        log.warn("No handler for: {} {}", ex.getHttpMethod(), ex.getResourcePath());
        return ApiResponses.entity(
                HttpStatus.NOT_FOUND,
                ResponseCode.NOT_FOUND,
                "Endpoint not found: " + ex.getHttpMethod() + " " + ex.getResourcePath()
                        + ". Use POST /api/v1/products/add (single slash, not //api)"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ApiResponses.entity(HttpStatus.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_ERROR);
    }
}
