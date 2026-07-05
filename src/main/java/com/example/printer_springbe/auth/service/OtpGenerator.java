package com.example.printer_springbe.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OtpGenerator {

    private final SecureRandom secureRandom = new SecureRandom();
    private final int otpLength;

    public OtpGenerator(@Value("${app.auth.otp.length:6}") int otpLength) {
        this.otpLength = otpLength;
    }

    public String generate() {
        int bound = (int) Math.pow(10, otpLength);
        int code = secureRandom.nextInt(bound);
        return String.format("%0" + otpLength + "d", code);
    }
}
