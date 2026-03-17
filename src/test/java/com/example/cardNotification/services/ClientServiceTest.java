package com.example.cardNotification.services;

import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    private ArgumentCaptor<Client> clientCaptor;

    private ClientService clientService;
    private Client testClient;
    private ClientRequestDto testClientRequestDto;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Петров Иван Иванович");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");

        testClientRequestDto = new ClientRequestDto(
                "Петров Иван Иванович",
                LocalDate.of(2000, 6, 1),
                "example@gmail.com"
        );
    }

    @Test
    void createClient_WithNewClient_ShouldCreateClientAndReturnSuccess() {
        when(clientRepository.findByNameAndBirthDate(
                testClientRequestDto.getFullName(),
                testClientRequestDto.getBirthDate()))
                .thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        ClientServiceDto result = clientService.createClient(testClientRequestDto);

        assertThat(result.isCreated()).isTrue();
        assertThat(result.getClientResponseDto()).isNotNull();
        assertThat(result.getClientResponseDto().getId()).isEqualTo(1L);
        assertThat(result.getClientResponseDto().getFullName()).isEqualTo("Петров Иван Иванович");
        assertThat(result.getClientResponseDto().getBirthDate()).isEqualTo(LocalDate.of(2000, 6, 1));
        assertThat(result.getClientResponseDto().getEmail()).isEqualTo("example@gmail.com");

        verify(clientRepository).findByNameAndBirthDate(
                testClientRequestDto.getFullName(),
                testClientRequestDto.getBirthDate()
        );
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void createClient_WithExistingClient_ShouldReturnExistingClientWithoutCreating() {
        when(clientRepository.findByNameAndBirthDate(
                testClientRequestDto.getFullName(),
                testClientRequestDto.getBirthDate()))
                .thenReturn(Optional.of(testClient));

        ClientServiceDto result = clientService.createClient(testClientRequestDto);

        assertThat(result.isCreated()).isFalse();
        assertThat(result.getClientResponseDto()).isNotNull();
        assertThat(result.getClientResponseDto().getId()).isEqualTo(1L);
        assertThat(result.getClientResponseDto().getFullName()).isEqualTo("Петров Иван Иванович");
        assertThat(result.getClientResponseDto().getEmail()).isEqualTo("example@gmail.com");

        verify(clientRepository).findByNameAndBirthDate(
                testClientRequestDto.getFullName(),
                testClientRequestDto.getBirthDate()
        );
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void createClient_ShouldMapAllFieldsCorrectly_WhenCreatingNewClient() {
        ClientRequestDto newClientRequest = new ClientRequestDto(
                "Андреев Евгений Александрович",
                LocalDate.of(2005, 11, 24),
                "evgeny@example.com"
        );

        Client savedClient = new Client();
        savedClient.setId(2L);
        savedClient.setFullName("Андреев Евгений Александрович");
        savedClient.setBirthDate(LocalDate.of(2005, 11, 24));
        savedClient.setEmail("evgeny@example.com");

        when(clientRepository.findByNameAndBirthDate(
                newClientRequest.getFullName(),
                newClientRequest.getBirthDate()))
                .thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(savedClient);

        ClientServiceDto result = clientService.createClient(newClientRequest);

        verify(clientRepository).save(clientCaptor.capture());
        Client capturedClient = clientCaptor.getValue();

        assertThat(capturedClient.getFullName()).isEqualTo("Андреев Евгений Александрович");
        assertThat(capturedClient.getBirthDate()).isEqualTo(LocalDate.of(2005, 11, 24));
        assertThat(capturedClient.getEmail()).isEqualTo("evgeny@example.com");

        assertThat(result.isCreated()).isTrue();
        assertThat(result.getClientResponseDto().getId()).isEqualTo(2L);
        assertThat(result.getClientResponseDto().getFullName()).isEqualTo("Андреев Евгений Александрович");
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