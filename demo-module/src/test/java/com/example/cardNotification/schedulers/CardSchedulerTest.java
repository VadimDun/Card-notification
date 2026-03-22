package com.example.cardNotification.schedulers;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.notifiers.CardNotifier;
import com.example.cardNotification.services.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardSchedulerTest {

    @Mock
    private CardService cardService;

    @Mock
    private CardNotifier cardNotifier;

    @InjectMocks
    private CardScheduler cardScheduler;

    @Captor
    private ArgumentCaptor<String> emailCaptor;

    @Captor
    private ArgumentCaptor<String> subjectCaptor;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Captor
    private ArgumentCaptor<Card> cardCaptor;

    private Client testClient;
    private Card expiredCard;
    private Card weekExpiringCard;
    private Card twoWeekExpiringCard;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Петров Иван Иванович");
        testClient.setEmail("example@gmail.com");

        expiredCard = new Card();
        expiredCard.setId(1L);
        expiredCard.setCardNumber("4200123456789012");
        expiredCard.setExpDate(LocalDate.now().minusDays(1));
        expiredCard.setActive(true);
        expiredCard.setNotified(false);
        expiredCard.setClient(testClient);

        weekExpiringCard = new Card();
        weekExpiringCard.setId(2L);
        weekExpiringCard.setCardNumber("2200123456789012");
        weekExpiringCard.setExpDate(LocalDate.now().plusWeeks(1));
        weekExpiringCard.setActive(true);
        weekExpiringCard.setNotified(false);
        weekExpiringCard.setClient(testClient);

        twoWeekExpiringCard = new Card();
        twoWeekExpiringCard.setId(3L);
        twoWeekExpiringCard.setCardNumber("5000123456789012");
        twoWeekExpiringCard.setExpDate(LocalDate.now().plusWeeks(2));
        twoWeekExpiringCard.setActive(true);
        twoWeekExpiringCard.setNotified(false);
        twoWeekExpiringCard.setClient(testClient);
    }

    @Test
    void checkExpiringCardsForNotification_ShouldProcessExpiredCards() {
        Card newCard = new Card();
        newCard.setId(4L);
        newCard.setCardNumber("4000123412341234");
        newCard.setExpDate(LocalDate.now().plusYears(4));
        newCard.setClient(testClient);

        when(cardService.getExpiredCardsAndNotNotified()).thenReturn(List.of(expiredCard));
        when(cardService.reissueCard(any(Card.class))).thenReturn(newCard);
        when(cardService.getCardsExpiringOn(any(LocalDate.class))).thenReturn(List.of());

        cardScheduler.checkExpiringCardsForNotification();

        verify(cardService).reissueCard(expiredCard);
        verify(cardNotifier).notifyClient(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                messageCaptor.capture()
        );
        verify(cardService).saveCard(cardCaptor.capture());

        assertThat(emailCaptor.getValue()).isEqualTo("example@gmail.com");
        assertThat(subjectCaptor.getValue()).isEqualTo("Срок действия вашей карты истек. Мы открыли новую карту");

        String message = messageCaptor.getValue();
        assertThat(message).contains("Петров Иван Иванович");
        assertThat(message).contains("4200123456789012");
        assertThat(message).contains("истек");
        assertThat(message).contains("4000123412341234");
        assertThat(message).contains(newCard.getExpDate().toString());

        Card savedCard = cardCaptor.getValue();
        assertThat(savedCard.isNotified()).isTrue();
    }

    @Test
    void checkExpiringCardsForNotification_ShouldProcessWeekBeforeExpired() {
        when(cardService.getExpiredCardsAndNotNotified()).thenReturn(List.of());
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(1)))
                .thenReturn(List.of(weekExpiringCard));
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(2)))
                .thenReturn(List.of());

        cardScheduler.checkExpiringCardsForNotification();

        verify(cardNotifier).notifyClient(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                messageCaptor.capture()
        );

        assertThat(emailCaptor.getValue()).isEqualTo("example@gmail.com");
        assertThat(subjectCaptor.getValue()).isEqualTo("Срок действия вашей карты истекает через неделю");

        String message = messageCaptor.getValue();
        assertThat(message).contains("Петров Иван Иванович");
        assertThat(message).contains("2200123456789012");
        assertThat(message).contains("истекает через неделю");
        assertThat(message).contains(LocalDate.now().plusWeeks(1).toString());

        verify(cardService, never()).reissueCard(any(Card.class));
        verify(cardService, never()).saveCard(any(Card.class));
    }

    @Test
    void checkExpiringCardsForNotification_ShouldProcessTwoWeekBeforeExpired() {
        when(cardService.getExpiredCardsAndNotNotified()).thenReturn(List.of());
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(1)))
                .thenReturn(List.of());
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(2)))
                .thenReturn(List.of(twoWeekExpiringCard));

        cardScheduler.checkExpiringCardsForNotification();

        verify(cardNotifier).notifyClient(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                messageCaptor.capture()
        );

        assertThat(emailCaptor.getValue()).isEqualTo("example@gmail.com");
        assertThat(subjectCaptor.getValue()).isEqualTo("Срок действия вашей карты истекает через 2 недели");

        String message = messageCaptor.getValue();
        assertThat(message).contains("Петров Иван Иванович");
        assertThat(message).contains("5000123456789012");
        assertThat(message).contains("истекает через 2 недели");
        assertThat(message).contains(LocalDate.now().plusWeeks(2).toString());

        verify(cardService, never()).reissueCard(any(Card.class));
        verify(cardService, never()).saveCard(any(Card.class));
    }

    @Test
    void checkExpiringCardsForNotification_ShouldProcessAllTypesSimultaneously() {
        Card newCard = new Card();
        newCard.setId(4L);
        newCard.setCardNumber("4000123412341234");
        newCard.setExpDate(LocalDate.now().plusYears(4));
        newCard.setClient(testClient);

        when(cardService.getExpiredCardsAndNotNotified()).thenReturn(List.of(expiredCard));
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(1)))
                .thenReturn(List.of(weekExpiringCard));
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(2)))
                .thenReturn(List.of(twoWeekExpiringCard));
        when(cardService.reissueCard(any(Card.class))).thenReturn(newCard);

        cardScheduler.checkExpiringCardsForNotification();

        verify(cardNotifier, times(3)).notifyClient(
                anyString(),
                anyString(),
                anyString()
        );
        verify(cardService).reissueCard(expiredCard);
        verify(cardService).saveCard(any(Card.class));
    }

    @Test
    void checkExpiringCardsForNotification_ShouldHandleEmptyLists() {
        when(cardService.getExpiredCardsAndNotNotified()).thenReturn(List.of());
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(1))).thenReturn(List.of());
        when(cardService.getCardsExpiringOn(LocalDate.now().plusWeeks(2))).thenReturn(List.of());

        cardScheduler.checkExpiringCardsForNotification();

        verify(cardNotifier, never()).notifyClient(
                anyString(),
                anyString(),
                anyString()
        );
        verify(cardService, never()).reissueCard(any(Card.class));
        verify(cardService, never()).saveCard(any(Card.class));
    }
}