package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.services.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable @Positive long id) {
        return clientService.getClientById(id)
                .map(client -> ResponseEntity.ok(ClientMapper.MapToResponse(client)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto clientDto) {
        ClientServiceDto response = clientService.createClient(clientDto);
        if (response.isCreated())
            return ResponseEntity.status(201).body(response.getClientResponseDto());
        return ResponseEntity.status(200).body(response.getClientResponseDto());
    }
}
