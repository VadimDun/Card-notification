package com.example.cardNotification.repositories.HashMapRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.repositories.CardRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Primary
public class HashMapCardRepository implements CardRepository {
    private final Map<Long, Card> cards = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    @Override
    public Card save(Card card) {
        if (card.getId() == null) {
            card.setId(idGen.incrementAndGet());
        }
        cards.put(card.getId(), card);
        return card;
    }

    @Override
    public Optional<Card> findById(Long id) {
        return Optional.ofNullable(cards.get(id));
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        return cards.values()
                .stream()
                .filter(c -> c.getCardNumber().equals(cardNumber))
                .findFirst();
    }

    @Override
    public List<Card> findByExpDate(LocalDate date) {
        return cards.values()
                .stream()
                .filter(c -> c.getExpDate().equals(date))
                .toList();
    }

    @Override
    public List<Card> findExpiredCards(){
        return cards.values()
                .stream()
                .filter(c -> !c.isActive())
                .toList();
    }

    @Override
    public List<Card> findAll() {
        return new ArrayList<>(cards.values());
    }
}
