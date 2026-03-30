package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClientRestController.class)
class ClientRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    private Client testClient;
    private ClientResponseDto testClientResponseDto;
    private ClientRequestDto testClientRequestDto;
    private ClientServiceDto testClientServiceDto;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Иван Иванов");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");

        testClientResponseDto = ClientMapper.MapToResponse(testClient);

        testClientRequestDto = new ClientRequestDto(
                "Иван Иванов",
                LocalDate.of(1990, 1, 1),
                "example@gmail.com"
        );

        testClientServiceDto = new ClientServiceDto();
        testClientServiceDto.setClientResponseDto(testClientResponseDto);
    }

    @Test
    void getAllClients_ShouldReturnListOfClientsSingle() throws Exception {
        List<ClientResponseDto> clients = Collections.singletonList(ClientMapper.MapToResponse(testClient));
        when(clientService.getClients(null)).thenReturn(clients);

        mockMvc.perform(get("/rest/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Иван Иванов")))
                .andExpect(jsonPath("$[0].email", is("example@gmail.com")));
    }

    @Test
    void getAllClients_ShouldReturnListOfClients() throws Exception {
        Client client2 = new Client();
        client2.setId(2L);
        client2.setFullName("Петр Петров");
        client2.setEmail("petr@example.com");
        client2.setBirthDate(LocalDate.of(1990, 1, 1));

        List<ClientResponseDto> clients = List.of(
                ClientMapper.MapToResponse(testClient),
                ClientMapper.MapToResponse(client2)
        );
        when(clientService.getClients(null)).thenReturn(clients);

        mockMvc.perform(get("/rest/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].fullName", is("Иван Иванов")))
                .andExpect(jsonPath("$[0].email", is("example@gmail.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].fullName", is("Петр Петров")))
                .andExpect(jsonPath("$[1].email", is("petr@example.com")));
    }

    @Test
    void getAllClients_ShouldReturnEmptyListWhenNoClients() throws Exception {
        when(clientService.getClients(null)).thenReturn(List.of());

        mockMvc.perform(get("/rest/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getClientById_WithExistingId_ShouldReturnClient() throws Exception {
        ClientServiceDto clientServiceDto = new ClientServiceDto();
        ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(testClient);
        clientServiceDto.setClientResponseDto(clientResponseDto);
        clientServiceDto.setExecuted(true);

        when(clientService.getClientById(1L)).thenReturn(clientServiceDto);

        mockMvc.perform(get("/rest/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иван Иванов")))
                .andExpect(jsonPath("$.email", is("example@gmail.com")));
    }

    @Test
    void getClientById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        ClientServiceDto clientServiceDto = new ClientServiceDto();
        clientServiceDto.setClientResponseDto(null);
        clientServiceDto.setExecuted(false);

        when(clientService.getClientById(999L)).thenReturn(clientServiceDto);

        mockMvc.perform(get("/rest/clients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getClientById_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/rest/clients/-1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createClient_WithNewClient_ShouldReturnCreated() throws Exception {
        testClientServiceDto.setExecuted(true);
        when(clientService.createClient(any(ClientRequestDto.class))).thenReturn(testClientServiceDto);

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testClientRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иван Иванов")))
                .andExpect(jsonPath("$.email", is("example@gmail.com")));
    }

    @Test
    void createClient_WithExistingClient_ShouldReturnOk() throws Exception {
        testClientServiceDto.setExecuted(false);
        when(clientService.createClient(any(ClientRequestDto.class))).thenReturn(testClientServiceDto);

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testClientRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fullName", is("Иван Иванов")));
    }

    @Test
    void createClient_WithBlankFullName_ShouldReturnBadRequest() throws Exception {
        ClientRequestDto invalidClient = new ClientRequestDto(
                "",
                LocalDate.of(1990, 1, 1),
                "example@gmail.com"
        );

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithTooShortFullName_ShouldReturnBadRequest() throws Exception {
        ClientRequestDto invalidClient = new ClientRequestDto(
                "A",
                LocalDate.of(1990, 1, 1),
                "example@gmail.com"
        );

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithFutureBirthDate_ShouldReturnBadRequest() throws Exception {
        ClientRequestDto invalidClient = new ClientRequestDto(
                "Иван Иванов",
                LocalDate.now().plusYears(1),
                "example@gmail.com"
        );

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        ClientRequestDto invalidClient = new ClientRequestDto(
                "Иван Иванов",
                LocalDate.of(1990, 1, 1),
                "invalid-email"
        );

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClient_WithNullBirthDate_ShouldReturnBadRequest() throws Exception {
        ClientRequestDto invalidClient = new ClientRequestDto(
                "Иван Иванов",
                null,
                "example@gmail.com"
        );

        mockMvc.perform(post("/rest/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteClient_WithExistingId_ShouldReturnNoContent() throws Exception {
        when(clientService.deleteById(1L)).thenReturn(true);

        mockMvc.perform(delete("/rest/clients/1"))
                .andExpect(status().isNoContent());

        verify(clientService, times(1)).deleteById(1L);
    }

    @Test
    void deleteClient_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(clientService.deleteById(999L)).thenReturn(false);

        mockMvc.perform(delete("/rest/clients/999"))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).deleteById(999L);
    }
}