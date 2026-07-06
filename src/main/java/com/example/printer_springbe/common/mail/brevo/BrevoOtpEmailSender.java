package com.example.printer_springbe.common.mail.brevo;

import com.example.printer_springbe.common.mail.OtpEmailDelivery;
import com.example.printer_springbe.common.mail.OtpEmailMessage;
import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.example.printer_springbe.common.mail.OtpEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BrevoOtpEmailSender implements OtpEmailSender {

    private static final Logger log = LoggerFactory.getLogger(BrevoOtpEmailSender.class);

    private final BrevoApiClient brevoApiClient;
    private final OtpEmailProperties mailProperties;

    public BrevoOtpEmailSender(BrevoApiClient brevoApiClient, OtpEmailProperties mailProperties) {
        this.brevoApiClient = brevoApiClient;
        this.mailProperties = mailProperties;
    }

    @Override
    public OtpEmailDelivery delivery() {
        return OtpEmailDelivery.BREVO;
    }

    @Override
    public void send(String toEmail, OtpEmailMessage message) {
        brevoApiClient.requireConfigured();
        String from = brevoApiClient.requireFromAddress(mailProperties);
        brevoApiClient.sendTransactionalEmail(from, toEmail, message);
        log.info("OTP sent via Brevo API to {}", mask(toEmail));
    }

    private static String mask(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
