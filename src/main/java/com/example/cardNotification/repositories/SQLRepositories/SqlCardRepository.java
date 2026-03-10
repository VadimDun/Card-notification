package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.repositories.CardRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlCardRepository implements CardRepository {
    private final JPACardRepository jpacardRepository;

    public SqlCardRepository(JPACardRepository jpacardRepository) {
        this.jpacardRepository = jpacardRepository;
    }

    @Override
    public Card save(Card card) {
        return jpacardRepository.save(card);
    }

    @Override
    public Optional<Card> findById(Long id) {
        return jpacardRepository.findById(id);
    }

    @Override
    public Optional<Card> findByCardNumber(String cardNumber) {
        return jpacardRepository.findByCardNumber(cardNumber);
    }

    @Override
    public List<Card> findByExpDate(LocalDate date) {
        return jpacardRepository.findByExpDate(date);
    }

    @Override
    public List<Card> findExpiredCards(){
        return jpacardRepository.findByActive(false);
    }

    @Override
    public List<Card> findAll() {
        return jpacardRepository.findAll();
    }
}
