package com.example.cardNotification.notifiers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailCardNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @InjectMocks
    private MailCardNotifier mailCardNotifier;

    @Test
    void notifyClient_ShouldSendEmail() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test email body";

        mailCardNotifier.notifyClient(to, subject, text);

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(text);
    }

    @Test
    void notifyClient_ShouldSendMultipleEmails() {
        mailCardNotifier.notifyClient("first@example.com", "Subject 1", "Text 1");
        mailCardNotifier.notifyClient("second@example.com", "Subject 2", "Text 2");

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

}