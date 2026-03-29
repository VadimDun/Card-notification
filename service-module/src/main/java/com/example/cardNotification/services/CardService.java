package com.example.cardNotification.services;

import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.dto.card.CardServiceDto;
import com.example.cardNotification.mappers.CardMapper;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.repositories.CardRepository;
import com.example.cardNotification.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final ClientRepository clientRepository;

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    public CardService(CardRepository cardRepository, ClientRepository clientRepository) {
        this.cardRepository = cardRepository;
        this.clientRepository = clientRepository;
    }

    public Card createCard(Card card) {
        while (cardRepository.findByCardNumber(card.getCardNumber()).isPresent()) {
            String newCardNumber = generateCardNumber();
            card.setCardNumber(newCardNumber);
        }

        logger.info("Создана карта {}", card);
        return cardRepository.save(card);
    }

    public CardServiceDto createCard(CardRequestDto cardRequestDto) {
        Optional<Client> client = clientRepository.findById(cardRequestDto.getClientId());

        CardServiceDto cardServiceDto = new CardServiceDto();

        if (client.isPresent()) {
            while (cardRepository.findByCardNumber(cardRequestDto.getCardNumber()).isPresent()) {
                String newCardNumber = generateCardNumber();
                cardRequestDto.setCardNumber(newCardNumber);
                
                cardServiceDto.setNumberChanged(true);
            }

            Card card = CardMapper.MapFromDto(cardRequestDto);

            card.setClient(client.get());
            card.setActive(!card.getExpDate().isBefore(LocalDate.now().plusDays(1)));
            card.setNotified(false);

            Card createdCard = cardRepository.save(card);
            CardResponseDto cardResponseDto = CardMapper.MapToResponse(createdCard);
            cardServiceDto.setCardResponseDto(cardResponseDto);
            cardServiceDto.setExecuted(true);
            logger.info("Создана карта {}",createdCard);
        }
        else {
            cardServiceDto.setExecuted(false);
            logger.warn("Карта для клиента с id:{} не создана - клиент не найден", cardRequestDto.getCardNumber());
        }

        return cardServiceDto;
    }

    public Card createCard(Long clientId, LocalDate issueDate, LocalDate expDate) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        String cardNumber = generateCardNumber();
        while (cardRepository.findByCardNumber(cardNumber).isPresent()) {
            cardNumber = generateCardNumber();
        }

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setIssueDate(issueDate);
        card.setExpDate(expDate);
        card.setActive(true);
        card.setClient(client);

        return cardRepository.save(card);
    }

    public CardServiceDto getCardById(Long id) {
        Optional<Card> cardOptional = cardRepository.findById(id);
        CardServiceDto cardServiceDto = new CardServiceDto();

        if (cardOptional.isPresent()) {
            Card card = cardOptional.get();
            CardResponseDto cardResponseDto = CardMapper.MapToResponse(card);

            cardServiceDto.setCardResponseDto(cardResponseDto);
            cardServiceDto.setExecuted(true);

            logger.info("Карта найдена по id {}: {}", id, card);
        } else {
            cardServiceDto.setCardResponseDto(null);
            cardServiceDto.setExecuted(false);

            logger.warn("Карта с id {} не найдена", id);
        }

        return cardServiceDto;
    }

    public void setNotified(Card card) {
        card.setNotified(true);
        saveCard(card);
    }

    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    public boolean closeCard(Long cardId) {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (cardOptional.isEmpty()) {
            logger.warn("Карта с id:{} не найдена для удаления", cardId);
            return false;
        }

        Card card = cardOptional.get();
        if (!card.isActive()){
            logger.warn("Карта с id:{} уже удалена", cardId);
            return false;
        }

        card.setActive(false);

        cardRepository.save(card);
        logger.info("Карта с id:{} удалена", cardId);
        return true;
    }

    public Card reissueCard(Card oldCard) {
        oldCard.setActive(false);
        cardRepository.save(oldCard);

        Card newCard = new Card();
        newCard.setCardNumber(generateCardNumber());
        newCard.setIssueDate(LocalDate.now());
        newCard.setExpDate(LocalDate.now().plusYears(4));
        newCard.setActive(true);
        newCard.setClient(oldCard.getClient());
        logger.info("Карта с id:{} закрыта. Перевыпущена новая карта {}",
                oldCard.getId(), newCard);

        return cardRepository.save(newCard);
    }

    public List<Card> getCardsExpiringOn(LocalDate date) {
        return cardRepository.findByExpDate(date);
    }

    public List<Card> getExpiredCardsAndNotNotified(){
        return cardRepository.findExpiredAndNotNotifiedCards();
    }

    public List<CardResponseDto> getAllCards() {
        return cardRepository.findAll().stream().map(CardMapper::MapToResponse).toList();
    }

    public List<CardResponseDto> getCards(Long clientId) {
        List<Card> cards;

        if (clientId != null) {
            cards = cardRepository.findByClientId(clientId);
        } else {
            cards = cardRepository.findAll();
        }

        return cards.stream().map(CardMapper::MapToResponse).toList();
    }

    public Optional<Card> findByCardNumber(String number) {
        return cardRepository.findByCardNumber(number);
    }

    public List<Card> searchCards(String number) {
        return cardRepository.findByCardNumberContaining(number);
    }

    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10)); // цифры от 0 до 9
        }

        return cardNumber.toString();
    }

    public boolean deleteById(long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
