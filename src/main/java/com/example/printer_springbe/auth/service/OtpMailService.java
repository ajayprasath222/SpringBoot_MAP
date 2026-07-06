package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.common.mail.OtpEmailComposer;
import com.example.printer_springbe.common.mail.OtpEmailDelivery;
import com.example.printer_springbe.common.mail.OtpEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OtpMailService {

    private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

    private final OtpEmailSender otpEmailSender;
    private final boolean logOtpInDev;

    public OtpMailService(
            OtpEmailSender otpEmailSender,
            @Value("${app.auth.log-otp-in-dev:true}") boolean logOtpInDev) {
        this.otpEmailSender = otpEmailSender;
        this.logOtpInDev = logOtpInDev;
    }

    public OtpEmailDelivery delivery() {
        return otpEmailSender.delivery();
    }

    public void sendOtp(String email, String otp, int expiresInSeconds) {
        if (logOtpInDev && otpEmailSender.delivery() == OtpEmailDelivery.EMBEDDED) {
            log.info("OTP for {} (expires in {}s): {}", mask(email), expiresInSeconds, otp);
        }
        otpEmailSender.send(email, OtpEmailComposer.otpMessage(otp, expiresInSeconds));
    }

    private static String mask(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
