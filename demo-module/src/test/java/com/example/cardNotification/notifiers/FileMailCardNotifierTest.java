package com.example.cardNotification.notifiers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileMailCardNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @InjectMocks
    private FileMailCardNotifier fileMailCardNotifier;

    @Test
    void notifyClient_ShouldWriteToFileAndSendEmail(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir.resolve("notifications.txt").toString();
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test message";

        fileMailCardNotifier = new FileMailCardNotifier(mailSender) {
            @Override
            public void notifyClient(String to, String subject, String text) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(to + "(" + subject + "): " + text + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(to);
                mailMessage.setSubject(subject);
                mailMessage.setText(text);
                mailSender.send(mailMessage);
            }
        };

        fileMailCardNotifier.notifyClient(to, subject, text);

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();
        assertThat(sentMessage.getTo()).containsExactly(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(text);

        Path path = Paths.get(filePath);
        assertThat(Files.exists(path)).isTrue();
        String content = Files.readString(path);
        assertThat(content).contains(to, subject, text);
    }

    @Test
    void notifyClient_ShouldAppendToFileForMultipleNotifications(@TempDir Path tempDir) throws IOException {
        String filePath = tempDir.resolve("notifications.txt").toString();

        fileMailCardNotifier = new FileMailCardNotifier(mailSender) {
            @Override
            public void notifyClient(String to, String subject, String text) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(to + "(" + subject + "): " + text + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(to);
                mailMessage.setSubject(subject);
                mailMessage.setText(text);
                mailSender.send(mailMessage);
            }
        };

        fileMailCardNotifier.notifyClient("first@example.com", "Subject 1", "Message 1");
        fileMailCardNotifier.notifyClient("second@example.com", "Subject 2", "Message 2");

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));

        String content = Files.readString(Paths.get(filePath));
        assertThat(content).contains("first@example.com", "Message 1");
        assertThat(content).contains("second@example.com", "Message 2");
        assertThat(content.lines().count()).isEqualTo(2);
    }

    @Test
    void notifyClient_ShouldSendEmailEvenIfFileWriteFails(@TempDir Path tempDir) {
        String to = "test@example.com";
        String subject = "Test";
        String text = "Message";

        fileMailCardNotifier.notifyClient(to, subject, text);

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage message = mailMessageCaptor.getValue();
        assertThat(message.getTo()).containsExactly(to);
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getText()).isEqualTo(text);
    }
}