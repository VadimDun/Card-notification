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
@ConditionalOnProperty(name = "cardNotifier.type", havingValue = "file")
public class FileCardNotifier implements CardNotifier {
    private static final String PATH = "notifications.txt";
    private static final Logger logger = LoggerFactory.getLogger(FileCardNotifier.class);

    @Async
    public void notifyClient(String to, String subject, String text) {
        try (FileWriter writer = new FileWriter(PATH, true)) {
            writer.write(to + "(" + subject + "): " + text + "\n");
            logger.info("Клиент {}({}):  уведомлен.", to, subject);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
