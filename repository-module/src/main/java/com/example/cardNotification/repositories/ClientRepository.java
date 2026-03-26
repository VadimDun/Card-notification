package com.example.cardNotification.repositories;

import com.example.cardNotification.models.Client;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findById(Long id);
    Optional<Client> findByNameAndBirthDate(String fullName, LocalDate birthDate);
    List<Client> findAll();
    List<Client> findByFullNameContaining(String namePart);
    boolean existsById(Long id);
    void deleteById(Long id);
}
