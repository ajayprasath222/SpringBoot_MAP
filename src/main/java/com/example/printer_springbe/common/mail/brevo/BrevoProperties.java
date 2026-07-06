package com.example.printer_springbe.common.mail.brevo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "brevo")
public class BrevoProperties {

    /**
     * Brevo API key (env: BREVO_API_KEY).
     */
    private String apiKey = "";

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(apiKey);
    }
}
