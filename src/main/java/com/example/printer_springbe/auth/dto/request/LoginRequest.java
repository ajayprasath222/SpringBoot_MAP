package com.example.printer_springbe.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 320)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 1, max = 128, message = "Password is required")
        String password
) {
}
