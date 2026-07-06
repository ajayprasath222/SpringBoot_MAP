package com.example.printer_springbe.common.mail;

import com.example.printer_springbe.common.mail.brevo.BrevoOtpEmailSender;
import com.example.printer_springbe.common.mail.embedded.EmbeddedOtpEmailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtpEmailConfiguration {

    @Bean
    public OtpEmailSender otpEmailSender(
            OtpEmailDeliveryResolver deliveryResolver,
            ObjectProvider<BrevoOtpEmailSender> brevoSender,
            ObjectProvider<EmbeddedOtpEmailSender> embeddedSender) {
        return deliveryResolver.isBrevo()
                ? brevoSender.getObject()
                : embeddedSender.getObject();
    }
}
