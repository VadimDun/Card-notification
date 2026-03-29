package com.example.cardNotification.controllers.rest;

import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Клиенты", description = "Действия с клиентами")
@RestController
@RequestMapping("/rest/clients")
public class ClientRestController {
    private final ClientService clientService;

    public ClientRestController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    @Operation(summary = "Получить клиентов по имени, либо всех существующих")
    public List<ClientResponseDto> getAllClients(@RequestParam(required = false) String name) {
        return clientService.getClients(name);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Найти клиента по id")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable @Positive long id) {
        ClientServiceDto response = clientService.getClientById(id);
        if (response.isExecuted()) {
            return ResponseEntity.status(200).body(response.getClientResponseDto());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Добавить клиента")
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto clientDto) {
        ClientServiceDto response = clientService.createClient(clientDto);
        if (response.isExecuted())
            return ResponseEntity.status(201).body(response.getClientResponseDto());
        return ResponseEntity.status(200).body(response.getClientResponseDto());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить клиента")
    public ResponseEntity<Void> deleteClient(@PathVariable @Positive long id) {
        if (clientService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
