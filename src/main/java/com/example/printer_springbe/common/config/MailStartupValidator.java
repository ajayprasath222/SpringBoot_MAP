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

    private final boolean embeddedMail;
    private final String mailHost;
    private final String mailUsername;
    private final String mailPassword;

    public MailStartupValidator(
            @Value("${app.mail.embedded:false}") boolean embeddedMail,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword) {
        this.embeddedMail = embeddedMail;
        this.mailHost = mailHost;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
    }

    @EventListener(ApplicationReadyEvent.class)
    void validate(ApplicationReadyEvent event) {
        if (embeddedMail) {
            log.info("""
                    
                    Mail mode: EMBEDDED (local dev).
                    OTP is NOT sent to Gmail inbox — read it from:
                      - Application console log
                      - GET http://localhost:8080/api/v1/dev/mails
                    For real inbox delivery use Brevo SMTP (see application-local-secrets.properties).
                    """);
            return;
        }

        if (!StringUtils.hasText(mailPassword)) {
            log.error("""
                    
                    ========== SMTP PASSWORD NOT SET ==========
                    OTP will NOT be emailed until you configure SMTP.
                    
                    Gmail App Passwords are NOT available on your account.
                    Use Brevo (free) instead:
                      1. https://www.brevo.com -> sign up
                      2. SMTP & API -> Generate SMTP key
                      3. Senders -> verify {}
                      4. In application-local-secrets.properties:
                         app.mail.embedded=false
                         spring.mail.host=smtp-relay.brevo.com
                         spring.mail.port=587
                         spring.mail.username=YOUR_BREVO_LOGIN_EMAIL
                         spring.mail.password=YOUR_BREVO_SMTP_KEY
                         app.mail.from={}
                      5. Restart the app
                    
                    Or for quick local testing set: app.mail.embedded=true
                    ============================================
                    """, mailUsername, mailUsername);
            return;
        }

        log.info("SMTP ready — sending as {} via {}", mailUsername, mailHost);
    }
}
