package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.repositories.CardRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "storage.type", havingValue = "sql")
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
        return jpacardRepository.findByExpDateLessThanEqual(LocalDate.now());
    }

    @Override
    public List<Card> findExpiredAndNotNotifiedCards(){
        return jpacardRepository.findByExpDateLessThanEqualAndNotifiedFalse(LocalDate.now());
    }

    @Override
    public List<Card> findAll() {
        return jpacardRepository.findAll();
    }

    @Override
    public List<Card> findByCardNumberContaining(String numberPart) {
        return jpacardRepository.findByCardNumberContaining(numberPart);
    }

    @Override
    public List<Card> findByClientId(Long clientId) {
        return jpacardRepository.findByClientId(clientId);
    }

    @Override
    public List<Card> findByClientIdAndCardNumberContaining(Long clientId, String cardNumber){
        return jpacardRepository.findByClientIdAndCardNumberContaining(clientId, cardNumber);
    }

    @Override
    public boolean existsById(Long id){
        return jpacardRepository.existsById(id);
    }

    @Override
    public void deleteById(long id){
        jpacardRepository.deleteById(id);
    }
}
