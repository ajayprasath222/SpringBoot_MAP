package com.example.printer_springbe.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;

@Configuration
public class MailConfig {

    private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

    private final AppMailProperties mailProperties;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;
    private final boolean embeddedMail;

    public MailConfig(
            AppMailProperties mailProperties,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword,
            @Value("${app.mail.embedded:false}") boolean embeddedMail) {
        this.mailProperties = mailProperties;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.embeddedMail = embeddedMail;
    }

    @PostConstruct
    void logMailStatus() {
        if (!mailProperties.isFromConfigured() && StringUtils.hasText(mailUsername)) {
            mailProperties.setFrom(mailUsername);
        }

        if (embeddedMail) {
            log.info("Mail mode: EMBEDDED (127.0.0.1). View OTP at GET /api/v1/dev/mails or in console logs.");
            return;
        }

        if (!StringUtils.hasText(mailHost) || !StringUtils.hasText(mailUsername) || !StringUtils.hasText(mailPassword)) {
            log.warn("""
                    Mail mode: SMTP but credentials are missing. Add application-local-secrets.properties
                    (see application-local-secrets.properties.example) or set MAIL_USERNAME / MAIL_PASSWORD.
                    """);
            return;
        }
        log.info("Mail mode: SMTP — host={}, from={}", mailHost, mailProperties.getFrom());
    }
}
