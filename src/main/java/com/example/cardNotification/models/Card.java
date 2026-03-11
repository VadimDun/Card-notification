package com.example.cardNotification.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cards")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expDate;
    private boolean active;
    private boolean isNotified;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

}
