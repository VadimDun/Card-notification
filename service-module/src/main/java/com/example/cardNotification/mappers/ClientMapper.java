package com.example.cardNotification.mappers;

import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.models.Client;

public class ClientMapper {

    public static Client MapFromDto(ClientRequestDto dto) {

        Client client = new Client();

        client.setFullName(dto.getFullName());
        client.setBirthDate(dto.getBirthDate());
        client.setEmail(dto.getEmail());

        return client;
    }

    public static ClientResponseDto MapToResponse(Client client) {
        ClientResponseDto dto = new ClientResponseDto();

        dto.setId(client.getId());
        dto.setBirthDate(client.getBirthDate());
        dto.setEmail(client.getEmail());
        dto.setFullName(client.getFullName());
        return dto;
    }

}
