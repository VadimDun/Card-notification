package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.ClientDto;
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
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients()
                .stream().map(ClientMapper::MapToDto).toList();
    }

    @GetMapping("/{id}")
    public ClientDto getClientById(@PathVariable long id) {
        return ClientMapper.MapToDto(clientService.getClientById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден")));
    }

    @PostMapping
    public ClientDto createClient(@RequestBody Client client) {
        return ClientMapper.MapToDto(clientService.createClient(client));
    }
}
