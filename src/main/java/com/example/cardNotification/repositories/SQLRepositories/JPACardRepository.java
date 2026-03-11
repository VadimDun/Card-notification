package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JPACardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardNumber(String cardNumber);
    List<Card> findByExpDate(LocalDate expDate);
    List<Card> findByActive(boolean active);
}

