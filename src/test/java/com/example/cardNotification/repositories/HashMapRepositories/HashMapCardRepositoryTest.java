package com.example.cardNotification.repositories.HashMapRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class HashMapCardRepositoryTest {

    private HashMapCardRepository cardRepository;
    private Client testClient;
    private Card activeCard;
    private Card expiredCard;
    private Card expiringTodayCard;

    @BeforeEach
    void setUp() {
        cardRepository = new HashMapCardRepository();

        testClient = new Client();
        testClient.setId(1L);
        testClient.setFullName("Петров Иван Иванович");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("ivan@gmail.com");

        activeCard = new Card();
        activeCard.setCardNumber("4200123456789012");
        activeCard.setIssueDate(LocalDate.now().minusYears(1));
        activeCard.setExpDate(LocalDate.now().plusYears(3));
        activeCard.setActive(true);
        activeCard.setNotified(false);
        activeCard.setClient(testClient);
        activeCard = cardRepository.save(activeCard);

        expiredCard = new Card();
        expiredCard.setCardNumber("2200123456789012");
        expiredCard.setIssueDate(LocalDate.now().minusYears(5));
        expiredCard.setExpDate(LocalDate.now().minusDays(1));
        expiredCard.setActive(false);
        expiredCard.setNotified(false);
        expiredCard.setClient(testClient);
        expiredCard = cardRepository.save(expiredCard);

        expiringTodayCard = new Card();
        expiringTodayCard.setCardNumber("5000123456789012");
        expiringTodayCard.setIssueDate(LocalDate.now().minusYears(4));
        expiringTodayCard.setExpDate(LocalDate.now());
        expiringTodayCard.setActive(false);
        expiringTodayCard.setNotified(false);
        expiringTodayCard.setClient(testClient);
        expiringTodayCard = cardRepository.save(expiringTodayCard);
    }

    @Test
    void save_ShouldUpdate_WhenCardExists() {
        activeCard.setCardNumber("0000000000000000");
        activeCard.setActive(false);

        Card updated = cardRepository.save(activeCard);

        assertThat(updated.getId()).isEqualTo(activeCard.getId());
        assertThat(updated.getCardNumber()).isEqualTo("0000000000000000");
        assertThat(updated.isActive()).isFalse();

        Optional<Card> found = cardRepository.findByCardNumber("0000000000000000");
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(activeCard.getId());
    }

    @Test
    void findById_ShouldReturnCard_WhenIdExists() {
        Optional<Card> found = cardRepository.findById(activeCard.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCardNumber()).isEqualTo(activeCard.getCardNumber());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        Optional<Card> found = cardRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findByCardNumber_ShouldReturnCard_WhenNumberExists() {
        Optional<Card> found = cardRepository.findByCardNumber("4200123456789012");

        assertThat(found).isPresent();
        assertThat(found.get().getCardNumber()).isEqualTo("4200123456789012");
        assertThat(found.get().getClient().getFullName()).isEqualTo("Петров Иван Иванович");
    }

    @Test
    void findByCardNumber_ShouldReturnEmpty_WhenNumberDoesNotExist() {
        Optional<Card> found = cardRepository.findByCardNumber("9999999999999999");

        assertThat(found).isEmpty();
    }

    @Test
    void findByExpDate_ShouldReturnCards_WithMatchingExpirationDate() {
        List<Card> cards = cardRepository.findByExpDate(LocalDate.now());

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getCardNumber()).isEqualTo("5000123456789012");
    }

    @Test
    void findByExpDate_ShouldReturnEmptyList_WhenNoCardsExpireOnDate() {
        List<Card> cards = cardRepository.findByExpDate(LocalDate.now().plusDays(10));

        assertThat(cards).isEmpty();
    }

    @Test
    void findExpiredCards_ShouldReturnAllExpiredCards() {
        List<Card> expiredCards = cardRepository.findExpiredCards();

        assertThat(expiredCards).hasSize(2);
        assertThat(expiredCards).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("2200123456789012", "5000123456789012");
    }

    @Test
    void findExpiredCards_ShouldNotReturnNonExpiredCards() {
        List<Card> expiredCards = cardRepository.findExpiredCards();

        assertThat(expiredCards).extracting(Card::getCardNumber)
                .doesNotContain("4200123456789012");
    }

    @Test
    void findExpiredAndNotNotifiedCards_ShouldReturnUnnotifiedExpiredCards() {
        List<Card> unnotifiedExpired = cardRepository.findExpiredAndNotNotifiedCards();

        assertThat(unnotifiedExpired).hasSize(2);
        assertThat(unnotifiedExpired).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("2200123456789012", "5000123456789012");
    }

    @Test
    void findExpiredAndNotNotifiedCards_ShouldNotReturnNotifiedCards() {
        expiredCard.setNotified(true);
        cardRepository.save(expiredCard);

        List<Card> unnotifiedExpired = cardRepository.findExpiredAndNotNotifiedCards();

        assertThat(unnotifiedExpired).hasSize(1);
        assertThat(unnotifiedExpired.get(0).getCardNumber()).isEqualTo("5000123456789012");
    }

    @Test
    void findAll_ShouldReturnAllCards() {
        List<Card> allCards = cardRepository.findAll();

        assertThat(allCards).hasSize(3);
        assertThat(allCards).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("4200123456789012", "2200123456789012", "5000123456789012");
    }

    @Test
    void findByCardNumberContaining_ShouldReturnMatchingCards() {
        List<Card> cards = cardRepository.findByCardNumberContaining("4200");

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getCardNumber()).isEqualTo("4200123456789012");
    }

    @Test
    void findByCardNumberContaining_ShouldReturnMultipleMatches() {
        List<Card> cards = cardRepository.findByCardNumberContaining("00");

        assertThat(cards).hasSize(3);
        assertThat(cards).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("4200123456789012", "2200123456789012", "5000123456789012");
    }

    @Test
    void findByCardNumberContaining_ShouldReturnEmptyList_WhenNoMatches() {
        List<Card> cards = cardRepository.findByCardNumberContaining("0000");

        assertThat(cards).isEmpty();
    }

    @Test
    void delete_ShouldRemoveCard() {
        Long cardId = activeCard.getId();

        cardRepository.delete(cardId);

        assertThat(cardRepository.findById(cardId)).isEmpty();
        assertThat(cardRepository.findAll()).hasSize(2);
    }

    @Test
    void delete_ShouldDoNothing_WhenIdDoesNotExist() {
        cardRepository.delete(999L);

        assertThat(cardRepository.findAll()).hasSize(3);
    }
}