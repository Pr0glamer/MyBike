package nix.project.mybike.services;

import nix.project.mybike.models.Debt;
import nix.project.mybike.repositories.DebtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DebtService {

    private final DebtsRepository debtsRepository;

    @Autowired
    public DebtService(DebtsRepository debtsRepository) {
        this.debtsRepository = debtsRepository;
    }

    int getTotalDebt(int client_id) {
        return debtsRepository.totalDebt(client_id);
    }

    public Debt findOne(int id) {
        Optional<Debt> foundDebt = debtsRepository.findById(id);
        return foundDebt.orElse(null);
    }
    @Transactional
    public void pay(Debt debt, int amount) {
        int result = debt.getAmount() - amount;
        if(result >= 0) {
            if(result == 0) {
                debtsRepository.delete(debt);
            } else {
                debt.setAmount(result);
                debtsRepository.save(debt);
            }
        }
    }

}
