package com.example.printer_springbe;

import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.example.printer_springbe.common.mail.brevo.BrevoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OtpEmailProperties.class, BrevoProperties.class})
public class PrinterSpringBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrinterSpringBeApplication.class, args);
    }
}
