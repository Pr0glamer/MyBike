package nix.project.mybike.controllers;


import nix.project.mybike.models.Debt;
import nix.project.mybike.services.DebtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/debts")
public class DebtController {

    private final DebtService debtService;

    @Autowired
    public DebtController(DebtService debtService) {
        this.debtService = debtService;
    }


    @PostMapping("/pay/{id}")
    public String pay(@PathVariable("id") int id,
                      @RequestParam("amount") int amount, Model model) {
        Debt debt     = debtService.findOne(id);
        model.addAttribute("client", debt.getOwner());
        model.addAttribute("bikes",  debt.getOwner().getBikes());


        if(debt.getAmount() < amount) {
            model.addAttribute("debts",  debt.getOwner().getDebts());
            model.addAttribute("errorMessage", "The payment amount cannot be greater than the debt amount.");
            return "clients/show";
        }

        debtService.pay(debt, amount);
        model.addAttribute("debts",  debt.getOwner().getDebts());

        return "clients/show";
    }
}
