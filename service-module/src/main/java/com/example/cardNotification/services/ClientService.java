package com.example.cardNotification.services;

import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        Optional<Client> clientOptional = clientRepository.findByNameAndBirthDate(client.getFullName(), client.getBirthDate());

        if (clientOptional.isPresent()) {
            logger.warn("Клиент уже существует: {}, возвращен старый клиент", client);
            return clientOptional.get();
        }

        Client savedClient = clientRepository.save(client);
        logger.info("Создан новый клиент: {}", savedClient);
        return savedClient;
    }

    public ClientServiceDto createClient(ClientRequestDto clientRequest) {
        Optional<Client> clientOptional = clientRepository.findByNameAndBirthDate(clientRequest.getFullName(), clientRequest.getBirthDate());
        ClientServiceDto clientSDto = new ClientServiceDto();

        if (clientOptional.isPresent()) {
            Client clientRes = clientOptional.get();
            ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(clientRes);

            clientSDto.setClientResponseDto(clientResponseDto);
            clientSDto.setExecuted(false);

            logger.warn("Клиент уже существует: {}, возвращен старый клиент", clientRes);
        }
        else{
            Client client = ClientMapper.MapFromDto(clientRequest);

            Client clientRes = clientRepository.save(client);
            ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(clientRes);

            clientSDto.setClientResponseDto(clientResponseDto);
            clientSDto.setExecuted(true);

            logger.info("Создан новый клиент: {}", clientRes);
        }
        return clientSDto;
    }

    public Client createClient(String fullName, LocalDate birthDate, String email) {
        Optional<Client> clientOptional =
                clientRepository.findByNameAndBirthDate(fullName, birthDate);

        if (clientOptional.isPresent()) {
            Client existingClient = clientOptional.get();
            logger.warn("Клиент уже существует: {}", existingClient);
            return existingClient;
        }

        Client client = new Client();
        client.setFullName(fullName);
        client.setBirthDate(birthDate);
        client.setEmail(email);

        Client savedClient = clientRepository.save(client);
        logger.info("Создан новый клиент: {}", savedClient);
        return savedClient;
    }

    public ClientServiceDto getClientById(Long id) {
        Optional<Client> clientOptional = clientRepository.findById(id);
        ClientServiceDto clientSDto = new ClientServiceDto();

        if (clientOptional.isPresent()) {
            Client clientRes = clientOptional.get();
            ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(clientRes);

            clientSDto.setClientResponseDto(clientResponseDto);
            clientSDto.setExecuted(true);

            logger.info("Клиент найден по id {}: {}", id, clientRes);
        } else {
            clientSDto.setExecuted(false);

            logger.warn("Клиент с id {} не найден", id);
        }

        return clientSDto;
    }

    public List<ClientResponseDto> getAllClients() {
        return clientRepository.findAll().stream().map(ClientMapper::MapToResponse).toList();
    }

    public Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
        return clientRepository.findByNameAndBirthDate(fullName, birthDate);
    }

    public List<Client> searchClients(String fullName) {
        return clientRepository.findByFullNameContaining(fullName);
    }

    public boolean deleteById(Long id){
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
