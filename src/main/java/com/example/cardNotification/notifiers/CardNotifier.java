package com.example.cardNotification.notifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
public class CardNotifier {
    private static final String PATH = "notifications.txt";
    private static final Logger logger = LoggerFactory.getLogger(CardNotifier.class);

    // Todo отправка на почту
    @Async
    public void notifyClient(String message) {
        try (FileWriter writer = new FileWriter(PATH, true)) {
            writer.write(message);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
