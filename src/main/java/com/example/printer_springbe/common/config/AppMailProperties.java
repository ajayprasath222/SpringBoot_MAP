package com.example.printer_springbe.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {

    /**
     * Mail delivery: auto (default), embedded, or smtp.
     * auto — SMTP when spring.mail.* credentials are set; otherwise embedded.
     */
    private String mode = MailModeResolver.MODE_AUTO;

    /**
     * Sender address (From header). Should match a verified sender in your SMTP provider.
     */
    private String from = "";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isFromConfigured() {
        return StringUtils.hasText(from);
    }
}
