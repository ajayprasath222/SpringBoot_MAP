package com.example.printer_springbe.auth.dto.response;

public record LoginResponse(
        Long userId,
        String email,
        String accessToken,
        String tokenType,
        int expiresInSeconds
) {
}
