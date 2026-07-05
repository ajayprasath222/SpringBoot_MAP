package com.example.printer_springbe.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Standard API envelope. Every successful or error response uses the same JSON shape:
 * <pre>
 * {
 *   "status": { "code": "000000", "description": "SUCCESS" },
 *   "data": { "Login": { ... } }
 * }
 * </pre>
 * Use {@link ApiResponses} to build instances.
 */
@JsonPropertyOrder({"status", "data"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private final ApiStatus status;
    private final Map<String, Object> data;

    ApiResponse(ApiStatus status, Map<String, Object> data) {
        this.status = status;
        this.data = data == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap<>(data));
    }

    public ApiStatus getStatus() {
        return status;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
