    package com.example.cardNotification.controllers;

    import com.example.cardNotification.models.Client;
    import com.example.cardNotification.services.ClientService;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;

    import java.util.List;
    import java.util.Optional;

    @Controller
    @RequestMapping("clients")
    public class ClientController {
        private final ClientService clientService;

        public ClientController(final ClientService clientService) {
            this.clientService = clientService;
        }

        @GetMapping
        public String index(final Model model) {
            model.addAttribute("clients", clientService.getAllClients());
            return "clients";
        }

        @GetMapping("/new")
        public String newClient(final Model model) {
            model.addAttribute("client", new Client());
            return "new-client";
        }

        @PostMapping
        public String addClient(@ModelAttribute("client") final Client client, RedirectAttributes redirectAttributes) {
//            boolean isDuplicate = clientService.getAllClients().stream().anyMatch(c -> c.equals(client));
            Optional<Client> isDuplicate = clientService.findByFullNameAndBirthDate(client.getFullName(), client.getBirthDate());
            if (isDuplicate.isPresent()) {
                redirectAttributes.addFlashAttribute("duplicateMessage",
                        "Такой клиент уже существует с id: "
                                + isDuplicate.get().getId());
            } else {
                Client savedClient = clientService.createClient(client);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Добавлен новый клиент с id: " + savedClient.getId());
            }
            return "redirect:/clients";
        }

        @PostMapping("/search")
        public String search(@RequestParam("searchTerm") String name, Model model) {
            List<Client> searchResults = clientService.searchClients(name);

            model.addAttribute("clients", searchResults);
            model.addAttribute("searchName", name);
            return "clients";
        }
    }
