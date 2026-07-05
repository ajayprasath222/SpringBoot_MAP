package com.example.printer_springbe.common.config;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * In-process SMTP for local development when no real SMTP credentials are configured.
 */
@Configuration
@Profile("local")
@Conditional(MailEmbeddedModeCondition.class)
public class EmbeddedMailServerConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMailServerConfig.class);

    public static final String DEV_USER = "dev@printer.local";
    public static final String DEV_PASSWORD = "dev";

    @Bean(destroyMethod = "stop")
    public GreenMail greenMail(@Value("${app.mail.embedded-smtp-port:3025}") int smtpPort) {
        ServerSetup smtp = new ServerSetup(smtpPort, "127.0.0.1", ServerSetup.PROTOCOL_SMTP);
        smtp.setServerStartupTimeout(10_000);
        GreenMail greenMail = new GreenMail(smtp);
        greenMail.setUser(DEV_USER, DEV_PASSWORD);
        greenMail.start();
        log.info("Embedded SMTP started on 127.0.0.1:{}", smtpPort);
        return greenMail;
    }

    @Bean
    @Primary
    public JavaMailSender embeddedJavaMailSender(
            @Value("${app.mail.embedded-smtp-port:3025}") int smtpPort,
            @Value("${app.mail.from:dev@printer.local}") String from) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("127.0.0.1");
        sender.setPort(smtpPort);
        sender.setUsername(DEV_USER);
        sender.setPassword(DEV_PASSWORD);
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");

        log.info("Mail mode: EMBEDDED — OTP in API response (data.SendOtp.otp) and GET /api/v1/dev/mails");
        log.info("Mail from: {}", from);
        return sender;
    }
}
