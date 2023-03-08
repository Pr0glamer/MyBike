package nix.project.mybike.controllers;


import nix.project.mybike.dao.DebtDTO;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.services.BikesService;
import nix.project.mybike.services.ClientService;
import nix.project.mybike.services.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/debts")
public class DebtController {

    private final ClientService clientService;
    private final BikesService bikesService;


    private final DebtService debtService;

    @Autowired
    public DebtController(ClientService clientService, BikesService bikesService, DebtService debtService) {
        this.clientService = clientService;
        this.bikesService = bikesService;
        this.debtService = debtService;
    }


    @PostMapping("/pay")
    public String pay(@RequestParam("amount") int amount,
                      @RequestParam("clientId")  int clientId,
                      @RequestParam("bikeId")  int bikeId, Model model) {


        Client client = clientService.findOne(clientId);
        Bike bike = bikesService.findById(bikeId);

        model.addAttribute("client",        client);
        model.addAttribute("bikes",         client.getBikes());
        model.addAttribute("reservations",  client.getReservations());

        int totalDebt = debtService.getTotalDebtForClientAndBike(client, bike);

        if(totalDebt < amount) {
            model.addAttribute("debts",  clientService.getDebtByClientId(client.getId()));
            model.addAttribute("errorMessage", "The payment amount cannot be greater than the debt amount.");
            return "clients/show";
        }

        debtService.pay(client, bike, -amount);
        model.addAttribute("debts",  clientService.getDebtByClientId(client.getId()));

        return "clients/show";
    }
    @GetMapping
    public String show(Model model) {
         Map<Client, List<DebtDTO>> debts = debtService.getDebtsGroupedByByClient();
        model.addAttribute("debts",  debts);
        return "debt/show";
    }

}
