package com.example.printer_springbe.common.config;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * In-process SMTP for local development when Gmail credentials are not configured.
 * OTP emails are captured here — view them at GET /api/v1/dev/mails or in the application log.
 */
@Configuration
@Profile("local")
@ConditionalOnProperty(name = "app.mail.embedded", havingValue = "true", matchIfMissing = true)
public class EmbeddedMailServerConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMailServerConfig.class);

    public static final String DEV_USER = "dev@printer.local";
    public static final String DEV_PASSWORD = "dev";

    @Bean(destroyMethod = "stop")
    public GreenMail greenMail(@Value("${app.mail.embedded-smtp-port:3025}") int smtpPort) {
        ServerSetup smtp = new ServerSetup(smtpPort, "127.0.0.1", ServerSetup.PROTOCOL_SMTP);
        GreenMail greenMail = new GreenMail(smtp);
        greenMail.setUser(DEV_USER, DEV_PASSWORD);
        greenMail.start();
        log.info("Embedded SMTP started on 127.0.0.1:{} (user={}, password={})", smtpPort, DEV_USER, DEV_PASSWORD);
        log.info("Local dev mail inbox: GET http://localhost:8080/api/v1/dev/mails");
        return greenMail;
    }
}
