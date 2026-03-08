package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface JPAClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByNameAndBirthDate(String fullName, LocalDate birthDate);
}
