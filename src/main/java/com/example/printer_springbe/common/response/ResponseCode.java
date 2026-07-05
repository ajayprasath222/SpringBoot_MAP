package com.example.printer_springbe.common.response;

/**
 * Application-wide response codes. Six-digit strings group outcomes by domain.
 * <ul>
 *   <li>000xxx — success</li>
 *   <li>010xxx — client / validation</li>
 *   <li>020xxx — authentication &amp; authorization</li>
 *   <li>030xxx — resources (e.g. not found)</li>
 *   <li>040xxx — business rules / conflict</li>
 *   <li>050xxx — server and infrastructure</li>
 * </ul>
 */
public enum ResponseCode {

    // Success (000xxx)
    SUCCESS("000000", "SUCCESS"),

    // Client / validation (010xxx)
    BAD_REQUEST("010000", "Bad request"),
    VALIDATION_FAILED("010001", "Validation failed"),
    UNSUPPORTED_MEDIA_TYPE("010002", "Unsupported media type"),

    // Authentication & authorization (020xxx)
    UNAUTHENTICATED("020000", "Authentication required"),
    INVALID_CREDENTIALS("020001", "Invalid credentials"),
    FORBIDDEN("020002", "Access denied"),
    TOKEN_EXPIRED("020003", "Token expired"),
    REGISTRATION_TOKEN_INVALID("020004", "Invalid or expired registration token"),
    OTP_INVALID("020010", "Invalid or expired OTP"),
    OTP_ATTEMPTS_EXCEEDED("020011", "Too many OTP attempts. Request a new code"),
    OTP_RESEND_TOO_SOON("020012", "Please wait before requesting another OTP"),

    // Resources (030xxx)
    NOT_FOUND("030000", "Resource not found"),

    // Business / conflict (040xxx)
    CONFLICT("040000", "Conflict"),
    EMAIL_ALREADY_REGISTERED("040001", "Email is already registered"),
    PASSWORD_MISMATCH("040002", "Password and confirm password do not match"),
    REGISTRATION_NOT_VERIFIED("040003", "Email verification is required before sign-up"),

    // Server (050xxx)
    INTERNAL_ERROR("050000", "Internal server error"),
    SERVICE_UNAVAILABLE("050001", "Service temporarily unavailable"),
    MAIL_NOT_CONFIGURED("050002", "Email service is not configured"),
    MAIL_DELIVERY_FAILED("050003", "Failed to send OTP email");

    private final String code;
    private final String defaultDescription;

    ResponseCode(String code, String defaultDescription) {
        this.code = code;
        this.defaultDescription = defaultDescription;
    }

    public String code() {
        return code;
    }

    public String defaultDescription() {
        return defaultDescription;
    }
}
