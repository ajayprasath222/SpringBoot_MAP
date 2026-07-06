package com.example.printer_springbe.common.mail.embedded;

import com.example.printer_springbe.common.mail.OtpEmailDelivery;
import com.example.printer_springbe.common.mail.OtpEmailMessage;
import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.example.printer_springbe.common.mail.OtpEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Conditional(EmbeddedMailCondition.class)
public class EmbeddedOtpEmailSender implements OtpEmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedOtpEmailSender.class);
    public static final String DEV_USER = "dev@printer.local";

    private final JavaMailSender mailSender;
    private final OtpEmailProperties mailProperties;

    public EmbeddedOtpEmailSender(JavaMailSender mailSender, OtpEmailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public OtpEmailDelivery delivery() {
        return OtpEmailDelivery.EMBEDDED;
    }

    @Override
    public void send(String toEmail, OtpEmailMessage message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailProperties.hasFromAddress() ? mailProperties.getFrom() : DEV_USER);
        mail.setTo(toEmail);
        mail.setSubject(message.subject());
        mail.setText(message.textBody());

        try {
            mailSender.send(mail);
            log.info("OTP captured locally for {} — see data.SendOtp.otp or GET /api/v1/dev/mails", mask(toEmail));
        } catch (MailException ex) {
            log.warn("Embedded mail capture failed for {}: {}", mask(toEmail), ex.getMessage());
        }
    }

    private static String mask(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
