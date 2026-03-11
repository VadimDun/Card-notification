package com.example.cardNotification.repositories;

import com.example.cardNotification.models.Card;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    Optional<Card> findById(Long id);
    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByExpDate(LocalDate date);
    List<Card> findExpiredCards();
    List<Card> findAll();
    void delete(long id);
}