package com.example.cardNotification.services;

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
        return clientRepository.save(client);
    }

    public Client createClient(String fullName, LocalDate birthDate) {
        Optional<Client> clientOptional =
                clientRepository.findByNameAndBirthDate(fullName, birthDate);

        if (clientOptional.isPresent()) {
            return clientOptional.get();
        }

        Client client = new Client();
        client.setFullName(fullName);
        client.setBirthDate(birthDate);

        return clientRepository.save(client);
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
}
