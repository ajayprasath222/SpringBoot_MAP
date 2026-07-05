package com.example.printer_springbe.auth.dto.response;

public record SendOtpResponse(
        String email,
        int expiresInSeconds,
        int resendAvailableInSeconds,
        String message,
        String deliveryMode,
        String devInboxUrl
) {
}
