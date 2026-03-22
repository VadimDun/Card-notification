package com.example.cardNotification.notifiers;

public interface CardNotifier {
    void notifyClient(String to, String subject, String text);
}
