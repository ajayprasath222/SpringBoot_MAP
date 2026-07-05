package com.example.printer_springbe.common.exception;

import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.http.HttpStatus;

/**
 * Domain-level failure mapped to a {@link ResponseCode} and HTTP status.
 */
public class BusinessException extends RuntimeException {

    private final ResponseCode responseCode;
    private final HttpStatus httpStatus;

    public BusinessException(ResponseCode responseCode, HttpStatus httpStatus) {
        super(responseCode.defaultDescription());
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
    }

    public BusinessException(ResponseCode responseCode, HttpStatus httpStatus, String message) {
        super(message);
        this.responseCode = responseCode;
        this.httpStatus = httpStatus;
    }

    public ResponseCode responseCode() {
        return responseCode;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
