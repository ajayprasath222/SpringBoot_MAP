package com.example.printer_springbe.common.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

class MailEmbeddedModeCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return MailModeResolver.resolve(context.getEnvironment()) == MailDeliveryMode.EMBEDDED;
    }
}
