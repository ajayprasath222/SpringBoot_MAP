package com.example.printer_springbe;

import com.example.printer_springbe.common.config.AppMailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppMailProperties.class)
public class PrinterSpringBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrinterSpringBeApplication.class, args);
    }

}
