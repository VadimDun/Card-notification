package com.example.cardNotification.services;

import com.example.cardNotification.dto.client.ClientServiceDto;
import com.example.cardNotification.dto.client.ClientRequestDto;
import com.example.cardNotification.dto.client.ClientResponseDto;
import com.example.cardNotification.mappers.ClientMapper;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        Optional<Client> clientOptional = clientRepository.findByNameAndBirthDate(client.getFullName(), client.getBirthDate());
        return clientOptional.orElseGet(() -> clientRepository.save(client));
    }

    public ClientServiceDto createClient(ClientRequestDto clientRequest) {
        Optional<Client> clientOptional = clientRepository.findByNameAndBirthDate(clientRequest.getFullName(), clientRequest.getBirthDate());
        ClientServiceDto clientSDto = new ClientServiceDto();

        if (clientOptional.isPresent()) {
            Client clientRes = clientOptional.get();
            ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(clientRes);

            clientSDto.setClientResponseDto(clientResponseDto);
            clientSDto.setCreated(false);
        }
        else{
            Client client = ClientMapper.MapFromDto(clientRequest);

            Client clientRes = clientRepository.save(client);
            ClientResponseDto clientResponseDto = ClientMapper.MapToResponse(clientRes);

            clientSDto.setClientResponseDto(clientResponseDto);
            clientSDto.setCreated(true);
        }
        return clientSDto;
    }

    public Client createClient(String fullName, LocalDate birthDate, String email) {
        Optional<Client> clientOptional =
                clientRepository.findByNameAndBirthDate(fullName, birthDate);

        if (clientOptional.isPresent()) {
            return clientOptional.get();
        }

        Client client = new Client();
        client.setFullName(fullName);
        client.setBirthDate(birthDate);
        client.setEmail(email);

        return clientRepository.save(client);
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
        return clientRepository.findByNameAndBirthDate(fullName, birthDate);
    }

    public List<Client> searchClients(String fullName) {
        return clientRepository.findByFullNameContaining(fullName);
    }
}
