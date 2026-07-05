package com.example.printer_springbe.auth.dto.response;

public record SignUpResponse(
        Long userId,
        String email,
        String accessToken,
        String tokenType,
        int expiresInSeconds
) {
}
