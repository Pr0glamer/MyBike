package nix.project.mybike.util;

import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class ClientValidator implements Validator {

    private final ClientService clientService;

    @Autowired
    public ClientValidator(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Client.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Client user = (Client) o;

        if (clientService.getUserByLogin(user.getLogin()).isPresent())
            errors.rejectValue("login", "", "User with login exists");
    }
}
