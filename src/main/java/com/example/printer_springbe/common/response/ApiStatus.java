package com.example.printer_springbe.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Status block returned on every API response: machine-readable {@code code} and human-readable
 * {@code description}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiStatus(String code, String description) {

    public static ApiStatus from(ResponseCode responseCode) {
        return new ApiStatus(responseCode.code(), responseCode.defaultDescription());
    }

    public static ApiStatus from(ResponseCode responseCode, String descriptionOverride) {
        return new ApiStatus(responseCode.code(), descriptionOverride);
    }
}
