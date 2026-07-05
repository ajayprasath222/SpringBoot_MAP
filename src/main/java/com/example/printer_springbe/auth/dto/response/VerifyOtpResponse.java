package com.example.printer_springbe.auth.dto.response;

public record VerifyOtpResponse(
        String email,
        String registrationToken,
        String tokenType,
        int expiresInSeconds
) {
}
