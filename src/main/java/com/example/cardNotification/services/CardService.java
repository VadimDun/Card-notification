package com.example.cardNotification.services;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.CardRepository;
import com.example.cardNotification.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;

    public CardService(CardRepository cardRepository, ClientRepository clientRepository) {
        this.cardRepository = cardRepository;
        this.clientRepository = clientRepository;
    }

    public Card createCard(Card card) {
        while (cardRepository.findByCardNumber(card.getCardNumber()).isPresent()) {
            String newCardNumber = generateCardNumber();
            card.setCardNumber(newCardNumber);
        }

        return cardRepository.save(card);
    }

    public Card createCard(Long clientId, LocalDate issueDate, LocalDate expDate) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        String cardNumber = generateCardNumber();
        while (cardRepository.findByCardNumber(cardNumber).isPresent()) {
            cardNumber = generateCardNumber();
        }

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setIssueDate(issueDate);
        card.setExpDate(expDate);
        card.setActive(true);
        card.setClient(client);

        return cardRepository.save(card);
    }

    public boolean cancelCard(Long cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (cardOptional.isEmpty()) {
            return false;
        }

        Card card = cardOptional.get();
        card.setActive(false);

        cardRepository.save(card);
        return true;
    }

    public Card reissueCard(Card oldCard) {
        oldCard.setActive(false);
        cardRepository.save(oldCard);

        Card newCard = new Card();
        newCard.setCardNumber(generateCardNumber());
        newCard.setIssueDate(LocalDate.now());
        newCard.setExpDate(LocalDate.now().plusYears(4));
        newCard.setActive(true);
        newCard.setClient(oldCard.getClient());

        return cardRepository.save(newCard);
    }

    public List<Card> getCardsExpiringOn(LocalDate date) {
        return cardRepository.findByExpDate(date);
    }

    public List<Card> getExpiredCardsAndNotNotified(){
        return cardRepository.findExpiredCards().stream().filter(c -> !c.isNotified()).toList();
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10)); // цифры от 0 до 9
        }

        return cardNumber.toString();
    }

    public void deleteById(long id) {
        cardRepository.delete(id);
    }
}
