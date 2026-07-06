package com.example.printer_springbe.common.mail;

import com.example.printer_springbe.common.mail.brevo.BrevoProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class OtpEmailDeliveryResolver {

    public static final String DELIVERY_EMBEDDED = "embedded";
    public static final String DELIVERY_BREVO = "brevo";
    public static final String DELIVERY_AUTO = "auto";

    private final OtpEmailDelivery delivery;

    public OtpEmailDeliveryResolver(OtpEmailProperties mailProperties, BrevoProperties brevoProperties, Environment environment) {
        this.delivery = resolve(mailProperties, brevoProperties, environment);
    }

    public OtpEmailDelivery getDelivery() {
        return delivery;
    }

    public boolean isEmbedded() {
        return delivery == OtpEmailDelivery.EMBEDDED;
    }

    public boolean isBrevo() {
        return delivery == OtpEmailDelivery.BREVO;
    }

    static OtpEmailDelivery resolve(OtpEmailProperties mailProperties, BrevoProperties brevoProperties, Environment environment) {
        String configured = mailProperties.getDelivery().trim().toLowerCase();
        return switch (configured) {
            case DELIVERY_EMBEDDED -> OtpEmailDelivery.EMBEDDED;
            case DELIVERY_BREVO -> OtpEmailDelivery.BREVO;
            default -> resolveAuto(brevoProperties, environment);
        };
    }

    private static OtpEmailDelivery resolveAuto(BrevoProperties brevoProperties, Environment environment) {
        if (brevoProperties.isConfigured() || StringUtils.hasText(environment.getProperty("BREVO_API_KEY"))) {
            return OtpEmailDelivery.BREVO;
        }
        if (environment.acceptsProfiles(Profiles.of("prod", "railway"))) {
            return OtpEmailDelivery.BREVO;
        }
        return OtpEmailDelivery.EMBEDDED;
    }
}
