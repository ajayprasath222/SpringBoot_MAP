package com.example.printer_springbe.common.mail;

public final class OtpEmailComposer {

    private static final String SUBJECT = "Your verification code";

    private OtpEmailComposer() {
    }

    public static OtpEmailMessage otpMessage(String otp, int expiresInSeconds) {
        String body = """
                Your one-time verification code is: %s

                This code expires in %d minutes.
                If you did not request this, you can ignore this email.
                """.formatted(otp, Math.max(1, expiresInSeconds / 60));
        return new OtpEmailMessage(SUBJECT, body);
    }
}
