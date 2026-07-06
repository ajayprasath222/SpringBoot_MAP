package com.example.printer_springbe.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailStartupValidator {

    private static final Logger log = LoggerFactory.getLogger(MailStartupValidator.class);

    private final MailChannelResolver mailChannelResolver;
    private final String mailHost;
    private final String mailPort;
    private final String mailFrom;
    private final String mailUsername;

    public MailStartupValidator(
            MailChannelResolver mailChannelResolver,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.port:}") String mailPort,
            @Value("${app.mail.from:}") String mailFrom,
            @Value("${spring.mail.username:}") String mailUsername) {
        this.mailChannelResolver = mailChannelResolver;
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailFrom = mailFrom;
        this.mailUsername = mailUsername;
    }

    @EventListener(ApplicationReadyEvent.class)
    void validate(ApplicationReadyEvent event) {
        if (mailChannelResolver.isEmbedded()) {
            log.info("Mail: EMBEDDED (local dev)");
            return;
        }
        if (mailChannelResolver.isBrevoApi()) {
            log.info("Mail: BREVO_API");
            return;
        }
        if (!StringUtils.hasText(mailUsername)) {
            log.error("""
                    
                    ========== SMTP NOT CONFIGURED ==========
                    Set in Railway Variables:
                      MAIL_HOST=smtp.gmail.com
                      MAIL_PORT=465
                      MAIL_SSL_ENABLE=true
                      MAIL_USERNAME=your@gmail.com
                      MAIL_PASSWORD=your-gmail-app-password
                      MAIL_FROM=your@gmail.com
                    =========================================
                    """);
            return;
        }
        log.info("Mail: SMTP via {}:{} — from {}", mailHost, mailPort, mailFrom);
        log.info("Note: Railway may block SMTP. If OTP times out, deploy on a VPS or use port 465.");
    }
}
