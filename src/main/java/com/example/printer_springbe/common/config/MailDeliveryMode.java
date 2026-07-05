package com.example.printer_springbe.common.config;

/**
 * How OTP emails are delivered.
 * <ul>
 *   <li>{@link #SMTP} — real inbox via configured SMTP (Gmail, Brevo, etc.)</li>
 *   <li>{@link #EMBEDDED} — local GreenMail; OTP also returned in API response</li>
 * </ul>
 */
public enum MailDeliveryMode {
    SMTP,
    EMBEDDED
}
