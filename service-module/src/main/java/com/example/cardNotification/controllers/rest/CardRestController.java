package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.dto.card.CardServiceDto;
import com.example.cardNotification.mappers.CardMapper;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.services.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("rest/cards")
public class CardRestController {
    private final CardService cardService;

    public CardRestController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    public List<CardResponseDto> getAllCards() {
        return cardService.getAllCards()
                .stream().map(CardMapper::MapToResponse).toList();
    }

    @PostMapping
    public ResponseEntity<?> createCard(@Valid @RequestBody CardRequestDto cardDto) {
        CardServiceDto response = cardService.createCard(cardDto);
        if (response.isCreated())
            return ResponseEntity.status(201).body(response.getCardResponseDto());
        else return ResponseEntity.status(404).body("Клиента с id: " + cardDto.getClientId().toString() + " не существует");
    }

    @PostMapping("/close/{id}")
    public ResponseEntity<Void> closeCard(@PathVariable @Positive long id) {
        if (cardService.closeCard(id))
            return ResponseEntity.noContent().build();
        else return ResponseEntity.notFound().build();
    }

}
