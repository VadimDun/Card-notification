package com.example.cardNotification.controllers;

import com.example.cardNotification.models.Card;
import com.example.cardNotification.models.Client;
import com.example.cardNotification.services.CardService;
import com.example.cardNotification.services.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;
    private final ClientService clientService;

    public CardController(final CardService cardService, final ClientService clientService) {
        this.cardService = cardService;
        this.clientService = clientService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("cards", cardService.getAllCards());
        return "cards";
    }

    @GetMapping("/new")
    public String newCard(Model model) {
        model.addAttribute("card", new Card());
        model.addAttribute("clients", clientService.getAllClients());
        return "new-card";
    }

    @PostMapping
    public String addCard(@ModelAttribute("card") Card card, @RequestParam("clientId") Long clientId) {
        if (card.getExpDate().isAfter(LocalDate.now()))
            card.setActive(true);

        Optional<Client> client = clientService.getClientById(clientId);
        client.ifPresent(card::setClient);

        cardService.createCard(card);
        return "redirect:/cards";
    }

    @PostMapping("/search")
    public String search(@RequestParam("searchTerm") String cardNumber, Model model) {
        List<Card> searchResults = cardService.getAllCards()
                .stream().filter(item -> item.getCardNumber().contains(cardNumber)).toList();

        model.addAttribute("cards", searchResults);
        model.addAttribute("searchNumber", cardNumber);
        return "cards";
    }
}
