package com.example.printer_springbe.common.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailChannelResolver {

    public static final String PROVIDER_PROPERTY = "app.mail.provider";
    public static final String PROVIDER_BREVO_API = "brevo-api";
    public static final String PROVIDER_SMTP = "smtp";

    private final MailDeliveryChannel channel;

    public MailChannelResolver(MailModeResolver mailModeResolver, Environment environment) {
        this.channel = resolve(mailModeResolver, environment);
    }

    public MailDeliveryChannel getChannel() {
        return channel;
    }

    public boolean isEmbedded() {
        return channel == MailDeliveryChannel.EMBEDDED;
    }

    public boolean isBrevoApi() {
        return channel == MailDeliveryChannel.BREVO_API;
    }

    public boolean isSmtp() {
        return channel == MailDeliveryChannel.SMTP;
    }

    static MailDeliveryChannel resolve(MailModeResolver mailModeResolver, Environment environment) {
        if (mailModeResolver.isEmbedded()) {
            return MailDeliveryChannel.EMBEDDED;
        }

        String provider = environment.getProperty(PROVIDER_PROPERTY, PROVIDER_SMTP).trim().toLowerCase();
        if (PROVIDER_BREVO_API.equals(provider)) {
            return MailDeliveryChannel.BREVO_API;
        }
        return MailDeliveryChannel.SMTP;
    }
}
