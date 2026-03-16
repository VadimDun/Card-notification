package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.CardDto;
import com.example.cardNotification.mappers.CardMapper;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.services.CardService;
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
    public List<CardDto> getAllCards() {
        return cardService.getAllCards()
                .stream().map(CardMapper::MapToDto).toList();
    }

    @PostMapping
    public CardDto createCard(@RequestBody Card card) {
        return CardMapper.MapToDto(cardService.createCard(card));
    }

    @PostMapping("close/{id}")
    public void closeCard(@PathVariable long id) {
        cardService.cancelCard(id);
    }

}
