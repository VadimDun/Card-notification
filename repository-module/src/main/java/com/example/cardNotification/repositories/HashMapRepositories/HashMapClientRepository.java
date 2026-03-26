package com.example.cardNotification.repositories.HashMapRepositories;

import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@ConditionalOnProperty(name = "storage.type", havingValue = "memory")
public class HashMapClientRepository implements ClientRepository {
    private final Map<Long, Client> clients = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    // testing
    @PostConstruct
    private void init() {
        Client client = new Client( 1L, "Вадим",
                LocalDate.of(2004, 7, 3), "example@gmail.com", List.of());
        client.setId(idGen.incrementAndGet());
        clients.put(client.getId(), client);
    }

    @Override
    public Client save(Client client) {
        if (client.getId() == null) {
            client.setId(idGen.incrementAndGet());
        }
        clients.put(client.getId(), client);
        return client;
    }

    @Override
    public Optional<Client> findById(Long id) {
        return Optional.ofNullable(clients.get(id));
    }

    @Override
    public Optional<Client> findByNameAndBirthDate(String fullName, LocalDate birthDate) {
        return clients.values()
                .stream()
                .filter(c -> c.getFullName().equals(fullName)
                        && c.getBirthDate().equals(birthDate))
                .findFirst();
    }

    @Override
    public List<Client> findAll() {
        return new ArrayList<>(clients.values());
    }

    @Override
    public List<Client> findByFullNameContaining(String namePart){
        return new ArrayList<>(clients.values().stream().filter(client -> client.getFullName().contains(namePart)).toList());
    }

    @Override
    public boolean existsById(Long id){
        return clients.containsKey(id);
    }

    @Override
    public void deleteById(Long id){
        clients.remove(id);
    }
}
