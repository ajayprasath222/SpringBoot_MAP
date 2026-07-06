package com.example.printer_springbe.common.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.mail")
public class OtpEmailProperties {

    /**
     * Verified sender address (must match Brevo sender).
     */
    private String from = "";

    /**
     * Delivery mode: auto (default), embedded, brevo.
     * auto — Brevo when API key is set; otherwise embedded (local dev).
     */
    private String delivery = "auto";

    private int embeddedSmtpPort = 3025;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public int getEmbeddedSmtpPort() {
        return embeddedSmtpPort;
    }

    public void setEmbeddedSmtpPort(int embeddedSmtpPort) {
        this.embeddedSmtpPort = embeddedSmtpPort;
    }

    public boolean hasFromAddress() {
        return StringUtils.hasText(from);
    }
}
