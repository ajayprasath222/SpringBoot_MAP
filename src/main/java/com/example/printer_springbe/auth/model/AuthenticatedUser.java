package com.example.printer_springbe.auth.model;

/**
 * Identity extracted from a valid JWT access token.
 */
public record AuthenticatedUser(Long userId, String email) {
}
