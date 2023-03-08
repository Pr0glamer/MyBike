package nix.project.mybike.services;

import nix.project.mybike.dao.DebtDTO;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.models.Debt;
import nix.project.mybike.repositories.DebtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DebtService {

    private final DebtsRepository debtsRepository;

    private final ClientService clientService;
    private final BikesService bikesService;

    @Autowired
    public DebtService(DebtsRepository debtsRepository, ClientService clientService, BikesService bikesService, ClientService clientService1, BikesService bikesService1) {
        this.debtsRepository = debtsRepository;
        this.clientService = clientService1;
        this.bikesService = bikesService1;
    }



    public Debt findOne(int id) {
        Optional<Debt> foundDebt = debtsRepository.findById(id);
        return foundDebt.orElse(null);
    }
    @Transactional
    public void pay(Client owner, Bike bike, int amount) {
        Debt debt = new Debt();
        debt.setBike(bike);
        debt.setOwner(owner);
        debt.setAmount(amount);
        debtsRepository.save(debt);
    }

    public List<DebtDTO> getAllDebtsFromDB() {
        var debts = debtsRepository.getAllDebts();
        return debts.stream().map(val-> {
                    Client client = clientService.findOne((int) val[1]);
                    Bike bike     = bikesService.findById((int) val[2]);
                    return new DebtDTO(client, bike, ((BigInteger) val[0]).intValue());
                }
        ).collect(Collectors.toList());
    }

    public int getTotalDebtForClientAndBike(Client client, Bike bike) {
       return debtsRepository.getTotalDebt(client.getId(), bike.getId());
    }

    public Map<Client, List<DebtDTO>> getDebtsGroupedByByClient() {
       return getAllDebtsFromDB().stream().collect(
                Collectors.groupingBy(DebtDTO::getClient));
    }
}
