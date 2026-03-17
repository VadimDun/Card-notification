package com.example.cardNotification.dto.card;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "Поле должно иметь 16 цифр")
    private String cardNumber;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate expDate;

    @NotNull
    private Long clientId;

}
