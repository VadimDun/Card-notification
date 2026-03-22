package com.example.cardNotification.controllers;

import com.example.cardNotification.dto.card.CardRequestDto;
import com.example.cardNotification.dto.card.CardResponseDto;
import com.example.cardNotification.dto.card.CardServiceDto;
import com.example.cardNotification.models.Card;
import com.example.cardNotification.services.CardService;
import com.example.cardNotification.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addAttribute("card", new CardRequestDto());
        model.addAttribute("clients", clientService.getAllClients());
        return "new-card";
    }

    @PostMapping
    public String addCard(@Valid @ModelAttribute("card") CardRequestDto cardDto,
                          BindingResult result,
                          @RequestParam("clientId") Long clientId,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("clients", clientService.getAllClients());
            return "new-card";
        }

        CardServiceDto response = cardService.createCard(cardDto);

        CardResponseDto cardResponseDto = response.getCardResponseDto();
        if (response.isNumberChanged()) {
            redirectAttributes.addFlashAttribute("message",
                    "Карта с таким номером уже существует, номер был изменен на "
                            + cardResponseDto.getCardNumber() + ". Добавлена новая карта с id: " + cardResponseDto.getId());
        } else {
            redirectAttributes.addFlashAttribute("message",
                    "Добавлена новая карта с id: " + cardResponseDto.getId());
        }

        return "redirect:/cards";
    }

    @PostMapping("/search")
    public String search(@RequestParam("searchTerm") String cardNumber, Model model) {
        List<Card> searchResults = cardService.searchCards(cardNumber);
        model.addAttribute("cards", searchResults);
        model.addAttribute("searchNumber", cardNumber);
        return "cards";
    }

    @PostMapping("/close/{id}")
    public String delete(@PathVariable("id") long id) {
        cardService.closeCard(id);
        return "redirect:/cards";
    }
}
