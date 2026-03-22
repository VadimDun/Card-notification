package com.example.cardNotification.notifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "cardNotifier.type", havingValue = "mail")
public class MailCardNotifier implements CardNotifier {
    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(MailCardNotifier.class);

    public MailCardNotifier(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void notifyClient(String to, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        mailSender.send(mailMessage);
        logger.info("Клиент {}({}):  уведомлен.", to, subject);
    }
}
