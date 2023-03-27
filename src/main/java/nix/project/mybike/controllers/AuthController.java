package nix.project.mybike.controllers;

import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import nix.project.mybike.util.ClientValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final ClientValidator clientValidator;
    private final ClientService clientService;

    @Autowired
    public AuthController(ClientValidator clientValidator, ClientService clientService) {
        this.clientValidator = clientValidator;
        this.clientService = clientService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("client") Client client) {
        return "auth/registration";

    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("client") Client client, BindingResult bindingResult) {
        clientValidator.validate(client, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/auth/registration";
        }

        clientService.save(client);

        return "redirect:/auth/login";

    }
}

