package com.example.cardNotification.notifiers;

import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> mailMessageCaptor;

    @InjectMocks
    private CardNotifier cardNotifier;

    @TempDir
    Path tempDir;

    @Test
    void notifyClient_ShouldWriteMessageToFile(@TempDir Path tempDir) throws IOException {
        String testMessage = "Test notification message";
        String filePath = tempDir.resolve("notifications.txt").toString();

        cardNotifier = new CardNotifier(mailSender) {
            @Override
            public void notifyClient(String message) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        cardNotifier.notifyClient(testMessage);

        Path path = Paths.get(filePath);
        assertThat(Files.exists(path)).isTrue();
        String content = Files.readString(path);
        assertThat(content).isEqualTo(testMessage);
    }

    @Test
    void notifyClient_ShouldAppendToExistingFile(@TempDir Path tempDir) throws IOException {
        String firstMessage = "First message\n";
        String secondMessage = "Second message\n";
        String filePath = tempDir.resolve("notifications.txt").toString();

        cardNotifier = new CardNotifier(mailSender) {
            @Override
            public void notifyClient(String message) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(message);
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        cardNotifier.notifyClient(firstMessage);
        cardNotifier.notifyClient(secondMessage);

        String content = Files.readString(Paths.get(filePath));
        assertThat(content).isEqualTo(firstMessage + secondMessage);
    }

    @Test
    void notifyByEmail_ShouldSendEmail() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test email body";

        cardNotifier.notifyByEmail(to, subject, text);

        verify(mailSender).send(mailMessageCaptor.capture());
        SimpleMailMessage sentMessage = mailMessageCaptor.getValue();

        assertThat(sentMessage.getTo()).containsExactly(to);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(text);
    }

    @Test
    void notifyByEmail_ShouldSendMultipleEmails() {
        cardNotifier.notifyByEmail("first@example.com", "Subject 1", "Text 1");
        cardNotifier.notifyByEmail("second@example.com", "Subject 2", "Text 2");

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
}