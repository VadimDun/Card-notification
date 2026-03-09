package com.example.cardNotification.notifiers;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component
public class CardNotifier {
    private static final String PATH = "notifications.txt";

    // Todo отправка на почту
    public void notifyClient(String message) {
        try (FileWriter writer = new FileWriter(PATH, true)) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
