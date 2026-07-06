package com.example.printer_springbe.common.mail;

public interface OtpEmailSender {

    OtpEmailDelivery delivery();

    void send(String toEmail, OtpEmailMessage message);
}
