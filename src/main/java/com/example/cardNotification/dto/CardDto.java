package com.example.cardNotification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private Long id;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expDate;
    private boolean active;

    private Long clientId;
    private String clientName;

}
