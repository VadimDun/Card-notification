package com.example.cardNotification.services;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.CardRepository;
import com.example.cardNotification.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;

    public CardService(CardRepository cardRepository, ClientRepository clientRepository) {
        this.cardRepository = cardRepository;
        this.clientRepository = clientRepository;
    }

    public Long createCard(Card card) {
        return cardRepository.save(card).getId();
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

    public List<Card> getCardsExpiringOn(LocalDate date) {
        return cardRepository.findByExpDate(date);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    private String generateCardNumber() {
        return UUID.randomUUID().toString();
    }
}
