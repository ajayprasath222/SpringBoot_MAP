package com.example.printer_springbe.controller;

import com.example.printer_springbe.common.response.ApiResponse;
import com.example.printer_springbe.common.response.ApiResponses;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Local-only inbox for OTP emails captured by the embedded SMTP server.
 */
@RestController
@Profile("local")
@ConditionalOnBean(GreenMail.class)
@RequestMapping("/api/v1/dev")
public class DevMailController {

    private final GreenMail greenMail;

    public DevMailController(GreenMail greenMail) {
        this.greenMail = greenMail;
    }

    @GetMapping("/mails")
    public ResponseEntity<ApiResponse> listMails() throws FolderException, MessagingException {
        Message[] messages = greenMail.getReceivedMessages();
        List<Map<String, Object>> items = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("from", addresses(message.getFrom()));
            item.put("to", addresses(message.getRecipients(Message.RecipientType.TO)));
            item.put("subject", message.getSubject());
            item.put("body", extractBody(message));
            items.add(item);
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("count", items.size());
        payload.put("messages", items);
        payload.put("hint", "Embedded mode — OTP is not sent to real inboxes. Add Gmail SMTP in application-local-secrets.properties (app.mail.mode=auto) and restart.");
        return ApiResponses.okEntity("DevMail", payload);
    }

    private static List<String> addresses(jakarta.mail.Address[] addresses) {
        if (addresses == null) {
            return List.of();
        }
        List<String> list = new ArrayList<>();
        for (jakarta.mail.Address address : addresses) {
            list.add(address.toString());
        }
        return list;
    }

    private static String extractBody(Message message) {
        try {
            Object content = message.getContent();
            if (content == null) {
                return "";
            }
            return content.toString();
        } catch (MessagingException | IOException ex) {
            return "";
        }
    }
}
