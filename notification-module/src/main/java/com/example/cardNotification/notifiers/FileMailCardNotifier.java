package com.example.cardNotification.notifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
@ConditionalOnProperty(name = "cardNotifier.type", havingValue = "fileMail")
public class FileMailCardNotifier implements CardNotifier {

    private static final String PATH = "notifications.txt";
    private static final Logger logger = LoggerFactory.getLogger(FileMailCardNotifier.class);

    private final JavaMailSender mailSender;

    public FileMailCardNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void notifyClient(String to, String subject, String text) {
        try (FileWriter writer = new FileWriter(PATH, true)) {
            writer.write(to + " (" + subject + "): " + text + "\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
        logger.info("Клиент {}({}):  уведомлен.", to, subject);
    }
}
