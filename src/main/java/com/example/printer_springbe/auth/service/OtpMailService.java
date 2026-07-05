package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.common.config.AppMailProperties;
import com.example.printer_springbe.common.config.MailModeResolver;
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
    private final AppMailProperties mailProperties;
    private final MailModeResolver mailModeResolver;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final boolean logOtpInDev;

    public OtpMailService(
            JavaMailSender mailSender,
            AppMailProperties mailProperties,
            MailModeResolver mailModeResolver,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${app.auth.log-otp-in-dev:true}") boolean logOtpInDev) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.mailModeResolver = mailModeResolver;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.logOtpInDev = logOtpInDev;
    }

    public void sendOtp(String email, String otp, int expiresInSeconds) {
        if (logOtpInDev && mailModeResolver.isEmbedded()) {
            log.info("OTP for {} (expires in {}s): {}", maskEmail(email), expiresInSeconds, otp);
        }

        assertMailConfigured();

        String from = resolveFromAddress();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Your verification code");
        message.setText("""
                Your one-time verification code is: %s

                This code expires in %d minutes.
                If you did not request this, you can ignore this email.
                """.formatted(otp, Math.max(1, expiresInSeconds / 60)));

        try {
            mailSender.send(message);
            if (mailModeResolver.isEmbedded()) {
                log.info("OTP captured locally for {} — see data.SendOtp.otp or GET /api/v1/dev/mails", maskEmail(email));
            } else {
                log.info("OTP email sent via SMTP to {}", maskEmail(email));
            }
        } catch (MailAuthenticationException ex) {
            log.error("SMTP authentication failed for {} via {}", maskEmail(email), mailHost, ex);
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    """
                    SMTP authentication failed. For Gmail use a 16-character App Password from \
                    https://myaccount.google.com/apppasswords — set spring.mail.password or \
                    GMAIL_APP_PASSWORD in application-local-secrets.properties."""
            );
        } catch (MailException ex) {
            if (mailModeResolver.isEmbedded()) {
                log.warn(
                        "Embedded SMTP could not capture OTP for {} — OTP is still valid (data.SendOtp.otp): {}",
                        maskEmail(email),
                        ex.getMessage()
                );
                return;
            }
            log.error("Failed to send OTP email to {}", maskEmail(email), ex);
            String hint = ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not send OTP email: " + hint
            );
        }
    }

    private void assertMailConfigured() {
        if (mailModeResolver.isEmbedded()) {
            return;
        }
        if (!StringUtils.hasText(mailHost)) {
            throw new BusinessException(
                    ResponseCode.MAIL_NOT_CONFIGURED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "spring.mail.host is not set. Configure SMTP in application-local-secrets.properties"
            );
        }
        if (!StringUtils.hasText(mailUsername) || !StringUtils.hasText(mailPassword)) {
            throw new BusinessException(
                    ResponseCode.MAIL_NOT_CONFIGURED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "SMTP credentials missing. Set spring.mail.username and spring.mail.password "
                            + "(or GMAIL_APP_PASSWORD) in application-local-secrets.properties"
            );
        }
    }

    private String resolveFromAddress() {
        if (mailProperties.isFromConfigured()) {
            return mailProperties.getFrom();
        }
        return mailUsername;
    }

    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
