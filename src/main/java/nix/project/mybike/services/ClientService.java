package nix.project.mybike.services;

import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.models.Debt;
import nix.project.mybike.repositories.DebtsRepository;
import nix.project.mybike.repositories.ClientsRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class ClientService {

    private final ClientsRepository clientsRepository;

    private final DebtService debtService;

    @Value("${constant.expired}")
    private int expired;

    @Autowired
    public ClientService(ClientsRepository clientsRepository, DebtsRepository debtsRepository, DebtService debtService) {
        this.clientsRepository = clientsRepository;
        this.debtService = debtService;
    }

    public List<Client> findAll() {
        return clientsRepository.findAll();
    }

    public Client findOne(int id) {
        Optional<Client> foundClient = clientsRepository.findById(id);
        return foundClient.orElse(null);
    }

    @Transactional
    public Client save(Client client) {
        return clientsRepository.save(client);
    }

    @Transactional
    public void update(int id, Client updatedClient) {
        updatedClient.setId(id);
        clientsRepository.save(updatedClient);
    }

    @Transactional
    public void delete(int id) {
        clientsRepository.deleteById(id);
    }

    public Optional<Client> getClientByFullName(String fullName) {
        return clientsRepository.findByFullName(fullName);
    }

    public Page<Client> findWithPagination(Integer page, Integer clientsPerPage, String sortField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, clientsPerPage, sort);

        return clientsRepository.findAll(pageable);

    }

    public List<Bike> getBikesByClientId(int id) {
        Optional<Client> client = clientsRepository.findById(id);

        if (client.isPresent()) {
            Hibernate.initialize(client.get().getBikes());

            client.get().getBikes().forEach(bike -> {
                var takenAt = bike.getTakenAt();
                long diffInMillies = 0;
                if(takenAt!=null) {
                    diffInMillies = Math.abs(takenAt.getTime() - new Date().getTime());
                 }

                if (diffInMillies > expired)
                    bike.setExpired(true);
            });

            return client.get().getBikes();
        }
        else {
            return Collections.emptyList();
        }
    }

    public List<Debt> getDebtByClientId(int id) {
        Optional<Client> client = clientsRepository.findById(id);
        if (client.isPresent()) {
            Hibernate.initialize(client.get().getDebts());
            return client.get().getDebts().stream().filter(d->d.getAmount() > 0).collect(Collectors.toList());
        }
        else {
            return Collections.emptyList();
        }
    }

    public void pay(Debt debt, int amount) {
        debtService.pay(debt, amount);
    }
}
