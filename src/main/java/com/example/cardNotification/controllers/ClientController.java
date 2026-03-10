    package com.example.cardNotification.controllers;

    import com.example.cardNotification.models.Client;
    import com.example.cardNotification.services.ClientService;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

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
        public String addClient(@ModelAttribute("client") final Client client) {
            clientService.createClient(client);
            return "redirect:/clients";
        }

        @PostMapping("/search")
        public String search(@RequestParam("searchTerm") String name, Model model) {
            List<Client> searchResults = clientService.getAllClients()
                    .stream().filter(item -> item.getFullName().toLowerCase().contains(name.toLowerCase())).toList();

            model.addAttribute("clients", searchResults);
            model.addAttribute("searchName", name);
            return "clients";
        }
    }
