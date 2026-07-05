package com.example.printer_springbe.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SendOtpResponse(
        String email,
        int expiresInSeconds,
        int resendAvailableInSeconds,
        String message,
        String deliveryMode,
        String devInboxUrl,
        /** Present only in local embedded-mail / dev mode — not sent to real inboxes. */
        String otp
) {
}
