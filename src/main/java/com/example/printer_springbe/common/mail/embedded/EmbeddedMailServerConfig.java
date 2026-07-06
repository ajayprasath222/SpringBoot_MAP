package com.example.printer_springbe.common.mail.embedded;

import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@Profile("local")
@Conditional(EmbeddedMailCondition.class)
public class EmbeddedMailServerConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedMailServerConfig.class);

    @Bean(destroyMethod = "stop")
    public GreenMail greenMail(OtpEmailProperties mailProperties) {
        int port = mailProperties.getEmbeddedSmtpPort();
        ServerSetup smtp = new ServerSetup(port, "127.0.0.1", ServerSetup.PROTOCOL_SMTP);
        smtp.setServerStartupTimeout(10_000);
        GreenMail greenMail = new GreenMail(smtp);
        greenMail.setUser(EmbeddedOtpEmailSender.DEV_USER, "dev");
        greenMail.start();
        log.info("Embedded SMTP started on 127.0.0.1:{}", port);
        return greenMail;
    }

    @Bean
    @Primary
    public JavaMailSender embeddedJavaMailSender(OtpEmailProperties mailProperties) {
        int port = mailProperties.getEmbeddedSmtpPort();
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("127.0.0.1");
        sender.setPort(port);
        sender.setUsername(EmbeddedOtpEmailSender.DEV_USER);
        sender.setPassword("dev");
        sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.starttls.enable", "false");

        return sender;
    }
}
