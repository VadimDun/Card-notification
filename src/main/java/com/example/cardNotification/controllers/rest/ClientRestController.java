package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.ClientDto;
import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.services.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientResponseDto> getAllClients() {
        return clientService.getAllClients()
                .stream().map(ClientMapper::MapToResponse).toList();
    }

    @GetMapping("/{id}")
    public ClientResponseDto getClientById(@PathVariable long id) {
        return ClientMapper.MapToResponse(clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден")));
    }

    @PostMapping
    public ClientResponseDto createClient(@RequestBody ClientRequestDto clientDto) {
        Client client = ClientMapper.MapFromDto(clientDto);
        Client createdClient = clientService.createClient(client);
        return ClientMapper.MapToResponse(clientService.createClient(createdClient));
    }
}
