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

    private final MailModeResolver mailModeResolver;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;

    public MailStartupValidator(
            MailModeResolver mailModeResolver,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword) {
        this.mailModeResolver = mailModeResolver;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    void validate(ApplicationReadyEvent event) {
        if (mailModeResolver.isEmbedded()) {
            log.info("""
                    
                    Mail mode: EMBEDDED (auto — no SMTP credentials in secrets).
                    OTP is NOT sent to your real Gmail inbox.
                    Get OTP from: data.SendOtp.otp in send-otp response, console log, or GET /api/v1/dev/mails
                    
                    To send to real inbox, add Gmail/Brevo SMTP in application-local-secrets.properties
                    (see application-local-secrets.properties.example) and restart.
                    """);
            return;
        }

        if (!StringUtils.hasText(mailPassword)) {
            log.error("""
                    
                    ========== SMTP PASSWORD NOT SET ==========
                    app.mail.mode=smtp but spring.mail.password is missing.
                    Set spring.mail.password or GMAIL_APP_PASSWORD in application-local-secrets.properties
                    ============================================
                    """);
            return;
        }

        log.info("Mail mode: SMTP — OTP will be delivered to the recipient inbox via {}", mailHost);
        log.info("SMTP user: {}", mailUsername);
    }
}
