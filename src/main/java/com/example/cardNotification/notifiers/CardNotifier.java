package com.example.cardNotification.notifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
public class CardNotifier {
    private static final String PATH = "notifications.txt";
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(CardNotifier.class);

    public CardNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void notifyClient(String message) {
        try (FileWriter writer = new FileWriter(PATH, true)) {
            writer.write(message);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void notifyByEmail(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
    }
}
