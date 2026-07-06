package com.example.printer_springbe.auth.service;

import com.example.printer_springbe.common.config.AppMailProperties;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrevoApiMailSender {

    private static final Logger log = LoggerFactory.getLogger(BrevoApiMailSender.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String apiKey;
    private final AppMailProperties mailProperties;
    private final String defaultFrom;

    public BrevoApiMailSender(
            AppMailProperties mailProperties,
            @Value("${brevo.api-key:}") String apiKey,
            @Value("${BREVO_API_KEY:}") String brevoApiKeyEnv,
            @Value("${MAIL_FROM:}") String defaultFrom) {
        this.mailProperties = mailProperties;
        this.apiKey = StringUtils.hasText(apiKey) ? apiKey.trim() : brevoApiKeyEnv.trim();
        this.defaultFrom = defaultFrom;
        this.restClient = RestClient.builder().build();
    }

    public void send(String to, String subject, String textBody) {
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException(
                    ResponseCode.MAIL_NOT_CONFIGURED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "BREVO_API_KEY is not set in Railway Variables. Sign up at https://www.brevo.com → SMTP & API → Generate API key."
            );
        }

        String from = resolveFromAddress();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sender", Map.of("email", from, "name", "Printer"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", subject);
        body.put("textContent", textBody);

        try {
            restClient.post()
                    .uri(BREVO_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("api-key", apiKey)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("OTP email sent via Brevo API to {}", maskEmail(to));
        } catch (RestClientResponseException ex) {
            log.error("Brevo API error {} for {}", ex.getStatusCode(), maskEmail(to), ex);
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Brevo API error: " + ex.getResponseBodyAsString()
            );
        } catch (Exception ex) {
            log.error("Brevo API failed for {}", maskEmail(to), ex);
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Could not send OTP via Brevo API: " + ex.getMessage()
            );
        }
    }

    private String resolveFromAddress() {
        if (mailProperties.isFromConfigured()) {
            return mailProperties.getFrom();
        }
        if (StringUtils.hasText(defaultFrom)) {
            return defaultFrom;
        }
        throw new BusinessException(
                ResponseCode.MAIL_NOT_CONFIGURED,
                HttpStatus.SERVICE_UNAVAILABLE,
                "MAIL_FROM is not set in Railway Variables. Use a sender verified in Brevo."
        );
    }

    private static String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
