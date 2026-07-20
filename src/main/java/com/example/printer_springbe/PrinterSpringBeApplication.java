package com.example.printer_springbe;

import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.example.printer_springbe.common.mail.brevo.BrevoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({OtpEmailProperties.class, BrevoProperties.class})
public class PrinterSpringBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrinterSpringBeApplication.class, args);
    }
}
