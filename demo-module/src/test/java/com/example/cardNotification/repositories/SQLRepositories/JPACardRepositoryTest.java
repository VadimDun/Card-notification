package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.SQLRepositories.JPACardRepository;
import com.example.cardNotification.repositories.SQLRepositories.JPAClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan("com.example.demo")
class JPACardRepositoryTest {

    @Autowired
    private JPACardRepository cardRepository;

    @Autowired
    private JPAClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient;
    private Card activeCard;
    private Card expiredCard;
    private Card expiringTodayCard;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setFullName("Петров Иван Иванович");
        testClient.setBirthDate(LocalDate.of(2000, 6, 1));
        testClient.setEmail("example@gmail.com");
        clientRepository.save(testClient);

        activeCard = new Card();
        activeCard.setCardNumber("4200123456789012");
        activeCard.setIssueDate(LocalDate.now().minusYears(1));
        activeCard.setExpDate(LocalDate.now().plusYears(3));
        activeCard.setActive(true);
        activeCard.setNotified(false);
        activeCard.setClient(testClient);
        cardRepository.save(activeCard);

        expiredCard = new Card();
        expiredCard.setCardNumber("2200123456789012");
        expiredCard.setIssueDate(LocalDate.now().minusYears(5));
        expiredCard.setExpDate(LocalDate.now().minusDays(1));
        expiredCard.setActive(false);
        expiredCard.setNotified(false);
        expiredCard.setClient(testClient);
        cardRepository.save(expiredCard);

        expiringTodayCard = new Card();
        expiringTodayCard.setCardNumber("5000123456789012");
        expiringTodayCard.setIssueDate(LocalDate.now().minusYears(4));
        expiringTodayCard.setExpDate(LocalDate.now());
        expiringTodayCard.setActive(false);
        expiringTodayCard.setNotified(false);
        expiringTodayCard.setClient(testClient);
        cardRepository.save(expiringTodayCard);

        entityManager.flush();
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
    void findByActive_ShouldReturnOnlyActiveCards() {
        List<Card> activeCards = cardRepository.findByActive(true);

        assertThat(activeCards).hasSize(1);
        assertThat(activeCards.get(0).getCardNumber()).isEqualTo("4200123456789012");
    }

    @Test
    void findByActive_ShouldReturnOnlyInactiveCards() {
        List<Card> inactiveCards = cardRepository.findByActive(false);

        assertThat(inactiveCards).hasSize(2);
        assertThat(inactiveCards).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("2200123456789012", "5000123456789012");
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
    void findByExpDateLessThanEqualAndNotifiedFalse_ShouldReturnUnnotifiedExpiredCards() {
        List<Card> unnotifiedExpired = cardRepository.findByExpDateLessThanEqualAndNotifiedFalse(LocalDate.now());

        assertThat(unnotifiedExpired).hasSize(2);
        assertThat(unnotifiedExpired).extracting(Card::getCardNumber)
                .containsExactlyInAnyOrder("2200123456789012", "5000123456789012");
    }

    @Test
    void findByExpDateLessThanEqualAndNotifiedFalse_ShouldNotReturnNotifiedCards() {
        expiredCard.setNotified(true);
        cardRepository.save(expiredCard);
        entityManager.flush();

        List<Card> unnotifiedExpired = cardRepository.findByExpDateLessThanEqualAndNotifiedFalse(LocalDate.now());

        assertThat(unnotifiedExpired).hasSize(1);
        assertThat(unnotifiedExpired.get(0).getCardNumber()).isEqualTo("5000123456789012");
    }

    @Test
    void save_ShouldPersistCardWithGeneratedId() {
        Card newCard = new Card();
        newCard.setCardNumber("9999888877776666");
        newCard.setIssueDate(LocalDate.now());
        newCard.setExpDate(LocalDate.now().plusYears(4));
        newCard.setActive(true);
        newCard.setNotified(false);
        newCard.setClient(testClient);

        cardRepository.save(newCard);

        Optional<Card> found = cardRepository.findById(newCard.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getClient().getId()).isEqualTo(testClient.getId());
    }

    @Test
    void deleteById_ShouldRemoveCard() {
        Long cardId = activeCard.getId();

        cardRepository.deleteById(cardId);

        Optional<Card> found = cardRepository.findById(cardId);
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllCards() {
        List<Card> allCards = cardRepository.findAll();

        assertThat(allCards).hasSize(3);
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
}