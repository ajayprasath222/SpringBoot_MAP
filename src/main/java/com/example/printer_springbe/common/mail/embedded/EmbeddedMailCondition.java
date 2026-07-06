package com.example.printer_springbe.common.mail.embedded;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Profiles;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class EmbeddedMailCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var env = context.getEnvironment();
        if (env.acceptsProfiles(Profiles.of("prod", "railway"))) {
            return false;
        }
        String delivery = env.getProperty("app.mail.delivery", "auto").trim().toLowerCase();
        if ("embedded".equals(delivery)) {
            return true;
        }
        if ("brevo".equals(delivery)) {
            return false;
        }
        return !StringUtils.hasText(env.getProperty("BREVO_API_KEY"))
                && !StringUtils.hasText(env.getProperty("brevo.api-key"));
    }
}
