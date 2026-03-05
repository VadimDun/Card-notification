package com.example.cardNotification.models;

import java.time.LocalDate;

public class Card {
    private Long id;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expDate;
    private boolean isActive;

    private Long clientId;
}
