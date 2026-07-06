package com.example.printer_springbe.common.mail.brevo;

import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.mail.OtpEmailMessage;
import com.example.printer_springbe.common.mail.OtpEmailProperties;
import com.example.printer_springbe.common.response.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class BrevoApiClient {

    private static final String SEND_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final BrevoProperties brevoProperties;

    public BrevoApiClient(BrevoProperties brevoProperties) {
        this.brevoProperties = brevoProperties;
        this.restClient = RestClient.builder().build();
    }

    public void sendTransactionalEmail(String from, String to, OtpEmailMessage message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("sender", Map.of("email", from, "name", "Printer"));
        body.put("to", List.of(Map.of("email", to)));
        body.put("subject", message.subject());
        body.put("textContent", message.textBody());

        try {
            restClient.post()
                    .uri(SEND_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("api-key", brevoProperties.getApiKey())
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Brevo API error: " + ex.getResponseBodyAsString()
            );
        } catch (Exception ex) {
            throw new BusinessException(
                    ResponseCode.MAIL_DELIVERY_FAILED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Brevo API request failed: " + ex.getMessage()
            );
        }
    }

    public void requireConfigured() {
        if (!brevoProperties.isConfigured()) {
            throw new BusinessException(
                    ResponseCode.MAIL_NOT_CONFIGURED,
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "BREVO_API_KEY is not set. Get a free key at https://www.brevo.com → SMTP & API."
            );
        }
    }

    public String requireFromAddress(OtpEmailProperties mailProperties) {
        if (mailProperties.hasFromAddress()) {
            return mailProperties.getFrom();
        }
        throw new BusinessException(
                ResponseCode.MAIL_NOT_CONFIGURED,
                HttpStatus.SERVICE_UNAVAILABLE,
                "MAIL_FROM is not set. Verify this sender in Brevo → Senders."
        );
    }
}
