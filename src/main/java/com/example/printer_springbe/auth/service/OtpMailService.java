package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.common.config.AppMailProperties;
import com.example.printer_springbe.common.config.MailChannelResolver;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OtpMailService {

    private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

    private final JavaMailSender mailSender;
    private final BrevoApiMailSender brevoApiMailSender;
    private final AppMailProperties mailProperties;
    private final MailChannelResolver mailChannelResolver;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final boolean logOtpInDev;

    public OtpMailService(
            JavaMailSender mailSender,
            BrevoApiMailSender brevoApiMailSender,
            AppMailProperties mailProperties,
            MailChannelResolver mailChannelResolver,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${app.auth.log-otp-in-dev:true}") boolean logOtpInDev) {
        this.mailSender = mailSender;
        this.brevoApiMailSender = brevoApiMailSender;
        this.mailProperties = mailProperties;
        this.mailChannelResolver = mailChannelResolver;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.logOtpInDev = logOtpInDev;
    }

    public void sendOtp(String email, String otp, int expiresInSeconds) {
        if (logOtpInDev && mailChannelResolver.isEmbedded()) {
            log.info("OTP for {} (expires in {}s): {}", maskEmail(email), expiresInSeconds, otp);
        }

        String subject = "Your verification code";
        String text = """
                Your one-time verification code is: %s

                This code expires in %d minutes.
                If you did not request this, you can ignore this email.
                """.formatted(otp, Math.max(1, expiresInSeconds / 60));

        if (mailChannelResolver.isBrevoApi()) {
            brevoApiMailSender.send(email, subject, text);
            return;
        }
        if (mailChannelResolver.isEmbedded()) {
            sendViaEmbeddedSmtp(email, subject, text);
            return;
        }
        sendViaSmtp(email, subject, text);
    }

    private void sendViaEmbeddedSmtp(String email, String subject, String text) {
        try {
            mailSender.send(buildMessage(email, subject, text));
            log.info("OTP captured locally for {}", maskEmail(email));
        } catch (MailException ex) {
            log.warn("Embedded SMTP failed for {}: {}", maskEmail(email), ex.getMessage());
        }
    }

    private void sendViaSmtp(String email, String subject, String text) {
        assertSmtpConfigured();
        try {
            mailSender.send(buildMessage(email, subject, text));
            log.info("OTP sent via SMTP to {}", maskEmail(email));
        } catch (MailAuthenticationException ex) {
            throw new BusinessException(ResponseCode.MAIL_DELIVERY_FAILED, HttpStatus.SERVICE_UNAVAILABLE,
                    "SMTP auth failed. On Railway use BREVO_API_KEY instead of Gmail SMTP.");
        } catch (MailException ex) {
            String hint = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            throw new BusinessException(ResponseCode.MAIL_DELIVERY_FAILED, HttpStatus.SERVICE_UNAVAILABLE,
                    "SMTP blocked on Railway. Set BREVO_API_KEY and MAIL_FROM in Railway Variables. " + hint);
        }
    }

    private SimpleMailMessage buildMessage(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.isFromConfigured() ? mailProperties.getFrom() : mailUsername);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    private void assertSmtpConfigured() {
        if (!StringUtils.hasText(mailHost) || !StringUtils.hasText(mailUsername) || !StringUtils.hasText(mailPassword)) {
            throw new BusinessException(ResponseCode.MAIL_NOT_CONFIGURED, HttpStatus.SERVICE_UNAVAILABLE,
                    "SMTP not configured. On Railway set BREVO_API_KEY and MAIL_FROM.");
        }
    }

    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        return at <= 1 ? "***" : email.charAt(0) + "***" + email.substring(at);
    }
}
