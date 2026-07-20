package com.example.printer_springbe.auth.model;

import java.time.Instant;

/**
 * Identity extracted from a valid JWT access token.
 */
public record AuthenticatedUser(Long userId, String email, String jti, Instant expiresAt) {
}
