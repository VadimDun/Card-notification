package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.dto.card.CardServiceDto;
import com.example.cardNotification.mappers.CardMapper;
import com.example.cardNotification.services.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Карты", description = "Операции с картами")
@RestController
@RequestMapping("/rest/cards")
public class CardRestController {
    private final CardService cardService;

    public CardRestController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @Operation(summary = "Получить все карты клиента, либо все существующие")
    public List<CardResponseDto> getAllCards(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String number) {
        return cardService.getCards(clientId, number);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти карту по id")
    public ResponseEntity<CardResponseDto> getCardById(@Positive @PathVariable Long id) {
        CardServiceDto response = cardService.getCardById(id);
        if (response.isExecuted())
            return ResponseEntity.status(200).body(response.getCardResponseDto());
        else return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Добавить новую карту")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardRequestDto cardDto) {
        CardServiceDto response = cardService.createCard(cardDto);
        if (response.isExecuted())
            return ResponseEntity.status(201).body(response.getCardResponseDto());
        return ResponseEntity.status(404).body("Клиента с id: " + cardDto.getClientId().toString() + " не существует");
    }

    @PostMapping("/issue/{clientId}")
    public ResponseEntity<?> issueCard(@PathVariable Long clientId) {
        CardServiceDto response = cardService.issueCard(clientId);
        if (response.isExecuted())
            return ResponseEntity.status(201).body(response.getCardResponseDto());
        return ResponseEntity.status(404).body("Клиента с id: " + clientId.toString() + " не существует");
    }

    @PostMapping("/close/{id}")
    @Operation(summary = "Закрыть карту")
    public ResponseEntity<Void> closeCard(@PathVariable @Positive long id) {
        if (!cardService.existById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (!cardService.activeById(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        cardService.closeCard(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить карту")
    public ResponseEntity<Void> deleteCard(@PathVariable @Positive long id) {
        if (!cardService.existById(id)) {
            return ResponseEntity.notFound().build();
        }

        if (cardService.activeById(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        cardService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
