package nix.project.mybike.controllers;


import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import nix.project.mybike.services.DebtService;
import nix.project.mybike.util.ClientValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final DebtService debtService;
    private final ClientValidator clientValidator;


    @Autowired
    public ClientController(ClientService clientService, DebtService debtService, ClientValidator clientValidator) {
        this.clientService = clientService;
        this.debtService = debtService;
        this.clientValidator = clientValidator;
    }


    @GetMapping()
    public String index(Model model, @RequestParam(value = "page", defaultValue = "1") Integer page,
                        @RequestParam(value = "clients_per_page", defaultValue = "3") Integer clientsPerPage,
                        @RequestParam(value = "sortField", defaultValue = "yearOfBirth") String sortField,
                        @RequestParam(value = "sortDir", defaultValue = "asc") String sortDirection) {



        Page<Client> clientPage = clientService.findWithPagination(page, clientsPerPage, sortField, sortDirection);

        model.addAttribute("currentPage",    page);
        model.addAttribute("totalPages",     clientPage.getTotalPages());
        model.addAttribute("totalItems",     clientPage.getTotalElements());
        model.addAttribute("clients",        clientPage.getContent());

        model.addAttribute("sortField",      sortField);
        model.addAttribute("sortDir",        sortDirection);
        model.addAttribute("reverseSortDir", sortDirection.equals("asc") ? "desc" : "asc");

        return "clients/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("client", clientService.findOne(id));
        model.addAttribute("bikes",  clientService.getBikesByClientId(id));
        model.addAttribute("debts",  clientService.getDebtByClientId(id));
        model.addAttribute("reservations",  clientService.getReservationsByClientId(id));


        return "clients/show";
    }



    @GetMapping("/new")
    public String newClient(@ModelAttribute("client") Client client) {
        return "clients/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("client") @Valid Client client,
                         BindingResult bindingResult) {
        clientValidator.validate(client, bindingResult);

        if (bindingResult.hasErrors())
            return "clients/new";

        clientService.save(client);
        return "redirect:/clients";
    }



    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("client", clientService.findOne(id));
        return "clients/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("client") @Valid Client client, BindingResult bindingResult,
                         @PathVariable("id") int id) {

        Client clientFromDataBase = clientService.findOne(id);
        if(clientFromDataBase != null) {
            clientFromDataBase.setFullName(client.getFullName());
            clientFromDataBase.setYearOfBirth(client.getYearOfBirth());
            clientFromDataBase.setTelephone(client.getTelephone());
            clientService.update(id, clientFromDataBase);
        }

        return "redirect:/clients";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        clientService.delete(id);
        return "redirect:/clients";
    }


}
