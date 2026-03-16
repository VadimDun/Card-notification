package com.example.cardNotification.dto.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {

    private String cardNumber;
    private LocalDate expDate;
    private Long clientId;

}
