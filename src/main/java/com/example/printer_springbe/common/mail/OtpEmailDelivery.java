package com.example.printer_springbe.common.mail;

/**
 * How OTP emails are delivered.
 */
public enum OtpEmailDelivery {
    /** Local GreenMail — OTP also returned in API response. */
    EMBEDDED,
    /** Brevo HTTP API — production / Railway (free tier). */
    BREVO
}
