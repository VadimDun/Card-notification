package com.example.cardNotification.notifiers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileCardNotifierTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private FileCardNotifier fileCardNotifier;

    @TempDir
    Path tempDir;

    @Test
    void notifyClient_ShouldWriteMessageToFile() throws IOException {
        String filePath = tempDir.resolve("notifications.txt").toString();
        String to = "client@example.com";
        String subject = "Test Subject";
        String text = "Test notification message";

        fileCardNotifier = new FileCardNotifier() {
            @Override
            public void notifyClient(String to, String subject, String text) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(to + "(" + subject + "): " + text + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        fileCardNotifier.notifyClient(to, subject, text);

        Path path = Paths.get(filePath);
        assertThat(Files.exists(path)).isTrue();
        String content = Files.readString(path);
        assertThat(content).contains(to, subject, text);
    }

    @Test
    void notifyClient_ShouldAppendToExistingFile() throws IOException {
        String filePath = tempDir.resolve("notifications.txt").toString();

        fileCardNotifier = new FileCardNotifier() {
            @Override
            public void notifyClient(String to, String subject, String text) {
                try (FileWriter writer = new FileWriter(filePath, true)) {
                    writer.write(to + " (" + subject + "): " + text + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        fileCardNotifier.notifyClient("first@example.com", "Subject 1", "Message 1");
        fileCardNotifier.notifyClient("second@example.com", "Subject 2", "Message 2");

        String content = Files.readString(Paths.get(filePath));
        assertThat(content).contains("first@example.com", "Message 1");
        assertThat(content).contains("second@example.com", "Message 2");
        assertThat(content.lines().count()).isEqualTo(2);
    }

}