package com.example.printer_springbe.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Factory helpers for {@link ApiResponse}. Prefer these over constructing maps by hand.
 */
public final class ApiResponses {

    private ApiResponses() {
    }

    /**
     * Success with a single named payload under {@code data}, e.g. {@code data.Login = ...}.
     */
    public static ApiResponse success(String dataKey, Object payload) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put(dataKey, payload);
        return new ApiResponse(ApiStatus.from(ResponseCode.SUCCESS), data);
    }

    /**
     * Success with multiple named sections under {@code data}.
     */
    public static ApiResponse success(Map<String, Object> data) {
        return new ApiResponse(ApiStatus.from(ResponseCode.SUCCESS), data == null ? Map.of() : data);
    }

    /**
     * Success with empty {@code data} object (e.g. delete OK with no body).
     */
    public static ApiResponse successEmpty() {
        return new ApiResponse(ApiStatus.from(ResponseCode.SUCCESS), Map.of());
    }

    public static ApiResponse failure(ResponseCode code) {
        return new ApiResponse(ApiStatus.from(code), Map.of());
    }

    public static ApiResponse failure(ResponseCode code, String descriptionOverride) {
        return new ApiResponse(ApiStatus.from(code, descriptionOverride), Map.of());
    }

    /**
     * Error with extra fields under {@code data} (e.g. field errors).
     */
    public static ApiResponse failure(ResponseCode code, String descriptionOverride, Map<String, Object> data) {
        return new ApiResponse(ApiStatus.from(code, descriptionOverride), data == null ? Map.of() : data);
    }

    public static ResponseEntity<ApiResponse> okEntity(String dataKey, Object payload) {
        return ResponseEntity.ok(success(dataKey, payload));
    }

    public static ResponseEntity<ApiResponse> okEntity(Map<String, Object> data) {
        return ResponseEntity.ok(success(data));
    }

    public static ResponseEntity<ApiResponse> entity(HttpStatus httpStatus, ResponseCode code) {
        return ResponseEntity.status(httpStatus).body(failure(code));
    }

    public static ResponseEntity<ApiResponse> entity(HttpStatus httpStatus, ResponseCode code, String descriptionOverride) {
        return ResponseEntity.status(httpStatus).body(failure(code, descriptionOverride));
    }

    public static ResponseEntity<ApiResponse> entity(HttpStatus httpStatus, ResponseCode code, String descriptionOverride, Map<String, Object> data) {
        return ResponseEntity.status(httpStatus).body(failure(code, descriptionOverride, data));
    }
}
