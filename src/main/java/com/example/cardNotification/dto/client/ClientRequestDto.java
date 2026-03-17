package com.example.cardNotification.dto.client;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDto {

    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;

    @NotNull
    @Past
    private LocalDate birthDate;

    @Email
    @NotBlank
    private String email;

}
