package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.controllers.rest.CardRestController;
import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.dto.card.CardServiceDto;
import com.example.cardNotification.mappers.CardMapper;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.services.CardService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CardRestController.class)
class CardRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CardService cardService;

    private Card testCard;
    private Client testClient;
    private CardResponseDto testCardResponseDto;
    private CardRequestDto testCardRequestDto;
    private CardServiceDto testCardServiceDto;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Иван Иванов");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("4200123412341234");
        testCard.setIssueDate(LocalDate.now());
        testCard.setExpDate(LocalDate.now().plusYears(4));
        testCard.setActive(true);
        testCard.setClient(testClient);

        testCardResponseDto = CardMapper.MapToResponse(testCard);

        testCardRequestDto = new CardRequestDto(
                "4200123412341234",
                LocalDate.now(),
                LocalDate.now().plusYears(4),
                1L
        );

        testCardServiceDto = new CardServiceDto();
        testCardServiceDto.setCardResponseDto(testCardResponseDto);
    }

    @Test
    void getAllCards_ShouldReturnListOfCards() throws Exception {
        List<Card> cards = Collections.singletonList(testCard);
        when(cardService.getAllCards()).thenReturn(cards);

        mockMvc.perform(get("/rest/cards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].cardNumber", is("4200123412341234")))
                .andExpect(jsonPath("$[0].active", is(true)))
                .andExpect(jsonPath("$[0].clientId", is(1)))
                .andExpect(jsonPath("$[0].clientName", is("Иван Иванов")));
    }

    @Test
    void getAllCards_ShouldReturnEmptyListWhenNoCards() throws Exception {
        when(cardService.getAllCards()).thenReturn(List.of());

        mockMvc.perform(get("/rest/cards"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createCard_WithValidData_ShouldReturnCreated() throws Exception {
        testCardServiceDto.setCreated(true);
        when(cardService.createCard(any(CardRequestDto.class))).thenReturn(testCardServiceDto);

        mockMvc.perform(post("/rest/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cardNumber", is("4200123412341234")))
                .andExpect(jsonPath("$.active", is(true)))
                .andExpect(jsonPath("$.clientId", is(1)));
    }

    @Test
    void createCard_WithNonExistentClient_ShouldReturnNotFound() throws Exception {
        testCardServiceDto.setCreated(false);
        when(cardService.createCard(any(CardRequestDto.class))).thenReturn(testCardServiceDto);

        mockMvc.perform(post("/rest/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Клиента с id: 1 не существует")));
    }

    @Test
    void createCard_WithInvalidCardNumber_ShouldReturnBadRequest() throws Exception {
        CardRequestDto invalidCard = new CardRequestDto(
                "123", // меньше 16 цифр
                LocalDate.now(),
                LocalDate.now().plusYears(4),
                1L
        );

        mockMvc.perform(post("/rest/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCard_WithNullIssueDate_ShouldReturnBadRequest() throws Exception {
        CardRequestDto invalidCard = new CardRequestDto(
                "4200123412341234",
                null,
                LocalDate.now().plusYears(4),
                1L
        );

        mockMvc.perform(post("/rest/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCard)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void closeCard_WithExistingId_ShouldReturnNoContent() throws Exception {
        when(cardService.closeCard(1L)).thenReturn(true);

        mockMvc.perform(post("/rest/cards/close/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void closeCard_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        when(cardService.closeCard(999L)).thenReturn(false);

        mockMvc.perform(post("/rest/cards/close/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void closeCard_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/rest/cards/close/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void closeCard_WithAlreadyClosedCard_ShouldReturnNotFound() throws Exception {
        when(cardService.closeCard(1L)).thenReturn(false);

        mockMvc.perform(post("/rest/cards/close/1"))
                .andExpect(status().isNotFound());
    }
}
