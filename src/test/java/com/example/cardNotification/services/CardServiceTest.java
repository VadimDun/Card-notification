package com.example.cardNotification.services;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.CardRepository;
import com.example.cardNotification.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ClientRepository clientRepository;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private CardService cardService;
    private Client testClient;
    private Card testCard;

    @BeforeEach
    void setUp() {
        cardService = new CardService(cardRepository, clientRepository);

        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Петров Иван Иванович");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("4200123456789012");
        testCard.setIssueDate(LocalDate.now().minusYears(1));
        testCard.setExpDate(LocalDate.now().plusYears(3));
        testCard.setActive(true);
        testCard.setNotified(false);
        testCard.setClient(testClient);
    }

    @Test
    void createCard_ShouldSaveCard_WhenCardNumberIsUnique() {
        when(cardRepository.findByCardNumber(testCard.getCardNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.createCard(testCard);

        assertThat(result).isEqualTo(testCard);
        verify(cardRepository).findByCardNumber("4200123456789012");
        verify(cardRepository).save(testCard);
    }

    @Test
    void createCard_ShouldGenerateNewNumber_WhenCardNumberExists() {
        Card existingCard = new Card();
        existingCard.setCardNumber("4200123456789012");

        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(existingCard))
                .thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.createCard(testCard);

        assertThat(result).isEqualTo(testCard);
        verify(cardRepository, times(2)).findByCardNumber(anyString());
        verify(cardRepository).save(testCard);
    }

    @Test
    void createCard_ShouldRetryUntilUniqueNumberFound() {
        when(cardRepository.findByCardNumber(anyString()))
                .thenReturn(Optional.of(new Card()))
                .thenReturn(Optional.of(new Card()))
                .thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        Card result = cardService.createCard(testCard);

        assertThat(result).isEqualTo(testCard);
        verify(cardRepository, times(3)).findByCardNumber(anyString());
        verify(cardRepository).save(testCard);
    }

    @Test
    void createCard_WithClientId_ShouldCreateCardForExistingClient() {
        Long clientId = 1L;
        LocalDate issueDate = LocalDate.now();
        LocalDate expDate = LocalDate.now().plusYears(4);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = cardService.createCard(clientId, issueDate, expDate);

        assertThat(result.getClient()).isEqualTo(testClient);
        assertThat(result.getIssueDate()).isEqualTo(issueDate);
        assertThat(result.getExpDate()).isEqualTo(expDate);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getCardNumber()).isNotNull();

        verify(clientRepository).findById(clientId);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_WithClientId_ShouldThrowException_WhenClientNotFound() {
        Long clientId = 999L;
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(clientId, LocalDate.now(), LocalDate.now().plusYears(4)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Client not found");

        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void cancelCard_ShouldDeactivateCard_WhenCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        boolean result = cardService.cancelCard(1L);

        assertThat(result).isTrue();
        assertThat(testCard.isActive()).isFalse();
        verify(cardRepository).save(testCard);
    }

    @Test
    void cancelCard_ShouldReturnFalse_WhenCardDoesNotExist() {
        when(cardRepository.findById(999L)).thenReturn(Optional.empty());

        boolean result = cardService.cancelCard(999L);

        assertThat(result).isFalse();
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void reissueCard_ShouldDeactivateOldCardAndCreateNew() {
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Card result = cardService.reissueCard(testCard);

        verify(cardRepository, times(2)).save(cardCaptor.capture());
        List<Card> savedCards = cardCaptor.getAllValues();

        Card oldCard = savedCards.get(0);
        Card newCard = savedCards.get(1);

        assertThat(oldCard.isActive()).isFalse();
        assertThat(oldCard.getCardNumber()).isEqualTo("4200123456789012");

        assertThat(newCard.isActive()).isTrue();
        assertThat(newCard.getCardNumber()).isNotEqualTo("4200123456789012");
        assertThat(newCard.getIssueDate()).isEqualTo(LocalDate.now());
        assertThat(newCard.getExpDate()).isEqualTo(LocalDate.now().plusYears(4));
        assertThat(newCard.getClient()).isEqualTo(testClient);
    }

    @Test
    void getCardsExpiringOn_ShouldReturnCardsWithMatchingExpirationDate() {
        LocalDate expDate = LocalDate.now().plusMonths(1);
        List<Card> expectedCards = List.of(testCard);

        when(cardRepository.findByExpDate(expDate)).thenReturn(expectedCards);

        List<Card> result = cardService.getCardsExpiringOn(expDate);

        assertThat(result).isEqualTo(expectedCards);
        verify(cardRepository).findByExpDate(expDate);
    }

    @Test
    void generateCardNumber_ShouldReturn16DigitNumber() throws Exception {
        Method generateMethod = CardService.class.getDeclaredMethod("generateCardNumber");
        generateMethod.setAccessible(true);

        String cardNumber = (String) generateMethod.invoke(cardService);

        assertThat(cardNumber).isNotNull();
        assertThat(cardNumber).hasSize(16);
        assertThat(cardNumber).matches("\\d{16}");
    }

    @Test
    void generateCardNumber_ShouldReturnDifferentNumbersOnEachCall() throws Exception {
        Method generateMethod = CardService.class.getDeclaredMethod("generateCardNumber");
        generateMethod.setAccessible(true);

        String cardNumber1 = (String) generateMethod.invoke(cardService);
        String cardNumber2 = (String) generateMethod.invoke(cardService);
        String cardNumber3 = (String) generateMethod.invoke(cardService);

        assertThat(cardNumber1).isNotEqualTo(cardNumber2);
        assertThat(cardNumber1).isNotEqualTo(cardNumber3);
        assertThat(cardNumber2).isNotEqualTo(cardNumber3);
    }
}