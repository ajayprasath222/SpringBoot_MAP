package com.example.printer_springbe.common.mail;

import com.example.printer_springbe.common.mail.brevo.BrevoApiClient;
import com.example.printer_springbe.common.mail.brevo.BrevoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class MailStartupLogger {

    private static final Logger log = LoggerFactory.getLogger(MailStartupLogger.class);

    private final OtpEmailDeliveryResolver deliveryResolver;
    private final OtpEmailProperties mailProperties;
    private final BrevoProperties brevoProperties;
    private final BrevoApiClient brevoApiClient;

    public MailStartupLogger(
            OtpEmailDeliveryResolver deliveryResolver,
            OtpEmailProperties mailProperties,
            BrevoProperties brevoProperties,
            BrevoApiClient brevoApiClient) {
        this.deliveryResolver = deliveryResolver;
        this.mailProperties = mailProperties;
        this.brevoProperties = brevoProperties;
        this.brevoApiClient = brevoApiClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    void logDeliveryMode(ApplicationReadyEvent event) {
        if (deliveryResolver.isEmbedded()) {
            log.info("OTP email: EMBEDDED (local) — use data.SendOtp.otp or GET /api/v1/dev/mails");
            return;
        }

        if (!brevoProperties.isConfigured()) {
            log.error("""
                    
                    ========== BREVO_API_KEY MISSING ==========
                    Production uses Brevo API. Set in Railway Variables:
                      BREVO_API_KEY = from https://www.brevo.com → SMTP & API
                      MAIL_FROM     = verified sender email
                    ===========================================
                    """);
            return;
        }

        if (!mailProperties.hasFromAddress()) {
            log.error("""
                    
                    ========== MAIL_FROM MISSING ==========
                    Set MAIL_FROM to a sender verified in Brevo.
                    =====================================
                    """);
            return;
        }

        log.info("OTP email: BREVO API — sender {}", mailProperties.getFrom());

        if (event.getApplicationContext().getEnvironment().acceptsProfiles(Profiles.of("prod", "railway"))) {
            brevoApiClient.requireConfigured();
        }
    }
}
