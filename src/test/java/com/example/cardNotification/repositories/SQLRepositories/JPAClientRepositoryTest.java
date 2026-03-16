package com.example.cardNotification.repositories.SQLRepositories;

import com.example.cardNotification.models.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JPAClientRepositoryTest {

    @Autowired
    private JPAClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp() {
        client1 = new Client();
        client1.setFullName("Петров Иван Иванович");
        client1.setBirthDate(LocalDate.of(2000, 6, 1));
        client1.setEmail("ivan@example.com");
        client1 = clientRepository.save(client1);

        client2 = new Client();
        client2.setFullName("Андреев Евгений Александрович");
        client2.setBirthDate(LocalDate.of(2005, 1, 18));
        client2.setEmail("andreev@example.com");
        client2 = clientRepository.save(client2);

        Client client3 = new Client();
        client3.setFullName("Иванов Петр Иванович");
        client3.setBirthDate(LocalDate.of(2008, 10, 10));
        client3.setEmail("petrovII@example.com");
        clientRepository.save(client3);

        entityManager.flush();
    }

    @Test
    void findByFullNameAndBirthDate_ShouldReturnClient_WhenMatchExists() {
        Optional<Client> found = clientRepository.findByFullNameAndBirthDate(
                "Петров Иван Иванович", LocalDate.of(2000, 6, 1));

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(client1.getId());
        assertThat(found.get().getEmail()).isEqualTo("ivan@example.com");
    }

    @Test
    void findByFullNameAndBirthDate_ShouldReturnEmpty_WhenNoMatch() {
        Optional<Client> found = clientRepository.findByFullNameAndBirthDate(
                "Батонов Денис Романович", LocalDate.of(2000, 6, 1));

        assertThat(found).isEmpty();
    }

    @Test
    void findByFullNameContaining_ShouldReturnMatchingClients() {
        List<Client> clients = clientRepository.findByFullNameContaining("Иван");

        assertThat(clients).hasSize(2);
        assertThat(clients).extracting(Client::getFullName)
                .containsExactlyInAnyOrder("Иванов Петр Иванович", "Петров Иван Иванович");
    }

    @Test
    void findByFullNameContaining_ShouldReturnSingleMatch() {
        List<Client> clients = clientRepository.findByFullNameContaining("Евгений");

        assertThat(clients).hasSize(1);
        assertThat(clients.get(0).getFullName()).isEqualTo("Андреев Евгений Александрович");
    }

    @Test
    void findByFullNameContaining_ShouldReturnEmptyList_WhenNoMatches() {
        List<Client> clients = clientRepository.findByFullNameContaining("Ксения");

        assertThat(clients).isEmpty();
    }

    @Test
    void save_ShouldPersistClientWithGeneratedId() {
        Client newClient = new Client();
        newClient.setFullName("Кузнецова Мария Викторовна");
        newClient.setBirthDate(LocalDate.of(2003, 11, 24));
        newClient.setEmail("kmaria@example.com");

        Client saved = clientRepository.save(newClient);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("Кузнецова Мария Викторовна");

        Optional<Client> found = clientRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("kmaria@example.com");
    }

    @Test
    void findById_ShouldReturnClient_WhenIdExists() {
        Optional<Client> found = clientRepository.findById(client1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Петров Иван Иванович");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        Optional<Client> found = clientRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllClients() {
        List<Client> allClients = clientRepository.findAll();

        assertThat(allClients).hasSize(3);
    }

    @Test
    void delete_ShouldRemoveClient() {
        Long clientId = client2.getId();

        clientRepository.delete(client2);

        Optional<Client> found = clientRepository.findById(clientId);
        assertThat(found).isEmpty();
        assertThat(clientRepository.findAll()).hasSize(2);
    }

    @Test
    void update_ShouldModifyExistingClient() {
        client1.setEmail("new.email@example.com");
        client1.setFullName("Петров Иван Даниилович");

        Client updated = clientRepository.save(client1);

        assertThat(updated.getEmail()).isEqualTo("new.email@example.com");
        assertThat(updated.getFullName()).isEqualTo("Петров Иван Даниилович");

        Optional<Client> found = clientRepository.findById(client1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("new.email@example.com");
    }
}