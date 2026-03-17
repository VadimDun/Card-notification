package com.example.cardNotification.mappers;

import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.models.Card;

public class CardMapper {

    public static Card MapFromDto(CardRequestDto dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setExpDate(dto.getExpDate());
        return card;
    }

    public static CardResponseDto MapToResponse(Card card) {
        CardResponseDto dto = new CardResponseDto();

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

}
