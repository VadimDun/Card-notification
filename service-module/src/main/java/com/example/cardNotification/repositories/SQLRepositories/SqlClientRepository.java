package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.ClientRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
//@Primary
@ConditionalOnProperty(name = "storage.type", havingValue = "sql")
public class SqlClientRepository implements ClientRepository {
    private final JPAClientRepository jpaClientRepository;

    public SqlClientRepository(JPAClientRepository jpaClientRepository) {
        this.jpaClientRepository = jpaClientRepository;
    }

    @Override
    public Client save(Client client) {
        return jpaClientRepository.save(client);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaClientRepository.findById(id);
    }

    @Override
    public Optional<Client> findByNameAndBirthDate(String fullName, LocalDate birthDate) {
        return jpaClientRepository.findByFullNameAndBirthDate(fullName, birthDate);
    }

    @Override
    public List<Client> findAll() {
        return jpaClientRepository.findAll();
    }

    @Override
    public List<Client> findByFullNameContaining(String namePart){
        return jpaClientRepository.findByFullNameContaining(namePart);
    }
}
