package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JPACardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByExpDate(LocalDate expDate);
    List<Card> findByActive(boolean active);
    List<Card> findByCardNumberContaining(String cardNumber);
    List<Card> findByExpDateLessThanEqual(LocalDate expDate);
    List<Card> findByExpDateLessThanEqualAndNotifiedFalse(LocalDate expDate);
    List<Card> findByClientId(Long clientId);
    List<Card> findByClientIdAndCardNumberContaining(Long clientId, String cardNumber);
}

