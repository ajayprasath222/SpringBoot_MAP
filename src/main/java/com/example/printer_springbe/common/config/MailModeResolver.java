package com.example.printer_springbe.common.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Resolves mail delivery mode from {@code app.mail.mode} and SMTP properties.
 * <p>
 * {@code auto} (default): use SMTP when real credentials are configured; otherwise embedded.
 */
@Component
public class MailModeResolver {

    public static final String MODE_PROPERTY = "app.mail.mode";
    public static final String MODE_AUTO = "auto";
    public static final String MODE_EMBEDDED = "embedded";
    public static final String MODE_SMTP = "smtp";

    private final MailDeliveryMode mode;

    public MailModeResolver(Environment environment) {
        this.mode = resolve(environment);
    }

    public MailDeliveryMode getMode() {
        return mode;
    }

    public boolean isEmbedded() {
        return mode == MailDeliveryMode.EMBEDDED;
    }

    public boolean isSmtp() {
        return mode == MailDeliveryMode.SMTP;
    }

    public static MailDeliveryMode resolve(Environment environment) {
        String configured = environment.getProperty(MODE_PROPERTY, MODE_AUTO).trim().toLowerCase();
        return switch (configured) {
            case MODE_EMBEDDED -> MailDeliveryMode.EMBEDDED;
            case MODE_SMTP -> MailDeliveryMode.SMTP;
            default -> isSmtpConfigured(environment) ? MailDeliveryMode.SMTP : MailDeliveryMode.EMBEDDED;
        };
    }

    /**
     * True when a non-local SMTP host, username, and password are all set
     * (e.g. Gmail or Brevo in application-local-secrets.properties or env vars).
     */
    public static boolean isSmtpConfigured(Environment environment) {
        String host = firstNonBlank(
                environment.getProperty("spring.mail.host"),
                environment.getProperty("MAIL_HOST"));
        String username = firstNonBlank(
                environment.getProperty("spring.mail.username"),
                environment.getProperty("MAIL_USERNAME"));
        String password = firstNonBlank(
                environment.getProperty("spring.mail.password"),
                environment.getProperty("GMAIL_APP_PASSWORD"),
                environment.getProperty("MAIL_PASSWORD"));

        if (!StringUtils.hasText(host) || !StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return false;
        }
        if (isLocalMailHost(host)) {
            return false;
        }
        return !EmbeddedMailServerConfig.DEV_USER.equalsIgnoreCase(username);
    }

    private static boolean isLocalMailHost(String host) {
        String normalized = host.trim().toLowerCase();
        return "127.0.0.1".equals(normalized)
                || "localhost".equals(normalized)
                || "::1".equals(normalized);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }
}
