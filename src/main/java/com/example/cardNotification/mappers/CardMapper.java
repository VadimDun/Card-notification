package com.example.cardNotification.mappers;

import com.example.cardNotification.dto.CardDto;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;

public class CardMapper {

    public static CardDto MapToDto(Card card) {

        CardDto dto = new CardDto();

        dto.setId(card.getId());
        dto.setCardNumber(card.getCardNumber());
        dto.setIssueDate(card.getIssueDate());
        dto.setExpDate(card.getExpDate());
        dto.setActive(card.isActive());

        if (card.getClient() != null) {
            dto.setClientId(card.getClient().getId());
            dto.setClientName(card.getClient().getFullName());
        }

        return dto;
    }

    public static Card MapFromDto(CardDto dto) {
        Card card = new Card();
        card.setId(dto.getId());
        card.setCardNumber(dto.getCardNumber());
        card.setIssueDate(dto.getIssueDate());
        card.setExpDate(dto.getExpDate());
        card.setActive(dto.isActive());
        return card;
    }
}
