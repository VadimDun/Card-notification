package com.example.cardNotification.services;

import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientService clientService;
    private Client testClient;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Петров Иван Иванович");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");
    }

    @Test
    void createClient_WithNameAndBirthDate_ShouldReturnExistingClient_WhenFound() {
        String fullName = "Петров Иван Иванович";
        LocalDate birthDate = LocalDate.of(2000, 6, 1);
        String email = "example@gmail.com";

        when(clientRepository.findByNameAndBirthDate(fullName, birthDate))
                .thenReturn(Optional.of(testClient));

        Client result = clientService.createClient(fullName, birthDate, email);

        assertThat(result).isEqualTo(testClient);
        verify(clientRepository).findByNameAndBirthDate(fullName, birthDate);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void createClient_WithNameAndBirthDate_ShouldCreateNewClient_WhenNotFound() {
        String fullName = "Андреев Евгений Александрович";
        LocalDate birthDate = LocalDate.of(2005, 11, 24);
        String email = "example@gmail.com";
        Client newClient = new Client();
        newClient.setFullName(fullName);
        newClient.setBirthDate(birthDate);
        newClient.setEmail(email);

        when(clientRepository.findByNameAndBirthDate(fullName, birthDate))
                .thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(newClient);

        Client result = clientService.createClient(fullName, birthDate, email);

        assertThat(result.getFullName()).isEqualTo(fullName);
        assertThat(result.getBirthDate()).isEqualTo(birthDate);
        verify(clientRepository).findByNameAndBirthDate(fullName, birthDate);
        verify(clientRepository).save(any(Client.class));
    }

}