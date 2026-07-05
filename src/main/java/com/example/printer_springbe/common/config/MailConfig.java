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
    private final MailModeResolver mailModeResolver;
    private final String mailHost;
    private final String mailUsername;

    public MailConfig(
            AppMailProperties mailProperties,
            MailModeResolver mailModeResolver,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername) {
        this.mailProperties = mailProperties;
        this.mailModeResolver = mailModeResolver;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
    }

    @PostConstruct
    void logMailStatus() {
        if (!mailProperties.isFromConfigured() && StringUtils.hasText(mailUsername)) {
            mailProperties.setFrom(mailUsername);
        }

        if (mailModeResolver.isEmbedded()) {
            log.info("Mail delivery: EMBEDDED (no real SMTP credentials detected)");
            return;
        }

        log.info("Mail delivery: SMTP — host={}, from={}", mailHost, mailProperties.getFrom());
    }
}
