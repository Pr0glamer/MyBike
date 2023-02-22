package nix.project.mybike.services;

import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.models.Debt;
import nix.project.mybike.repositories.BikesRepository;
import nix.project.mybike.repositories.DebtsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class BikesService {

    private final BikesRepository bikesRepository;

    private final DebtsRepository debtsRepository;

    @Value("${constant.expired}")
    private int expired;

    @Autowired
    public BikesService(BikesRepository booksRepository, DebtsRepository debtsRepository) {
        this.bikesRepository = booksRepository;
        this.debtsRepository = debtsRepository;
    }

    public List<Bike> findAll(boolean sortByYear) {
        if (sortByYear)
            return bikesRepository.findAll(Sort.by("year"));
        else
            return bikesRepository.findAll();
    }


    public Page<Bike> findWithPagination(Integer page, Integer bikesPerPage, String sortField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

         Pageable pageable = PageRequest.of(page - 1, bikesPerPage, sort);

         return bikesRepository.findAll(pageable);

    }

    public Page<Bike> findWithPaginationWithTitle(Integer page, Integer bikesPerPage, String sortField, String sortDirection, String title) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, bikesPerPage, sort);

        return bikesRepository.findByTitleStartingWith(title, pageable);

    }

    public Page<Bike> findWithPaginationWithTitleAndAvailability(Integer page, Integer bikesPerPage, String sortField, String sortDirection, String title) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, bikesPerPage, sort);

        return bikesRepository.findBikeAvailableAndByTitleLike(title, pageable);

    }

    public Page<Bike> findWithPaginationWithAvailability(Integer page, Integer bikesPerPage, String sortField, String sortDirection) {

        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page - 1, bikesPerPage, sort);

        return bikesRepository.findBikeAvailable(pageable);

    }

    public Bike findOne(int id) {
        Optional<Bike> foundBook = bikesRepository.findById(id);
        return foundBook.orElse(null);
    }

    public List<Bike> searchByTitle(String query, boolean available) {

        List<Bike> bikes = bikesRepository.findByTitleStartingWith(query);
        if(available) {
            var availableBikes = bikes.stream().filter(bike->!bike.isExpired()).collect(Collectors.toList());
            return availableBikes;
        }
        return bikes;

    }

    public List<Bike> searchByAvailability(boolean available) {
        if(available) {
            return bikesRepository.findAll().stream().filter(bike->!bike.isExpired()).collect(Collectors.toList());
        }
        return bikesRepository.findAll();
    }


    @Transactional
    public void save(Bike bike) {
        bikesRepository.save(bike);
    }

    @Transactional
    public void update(int id, Bike updatedBike) {
        Bike bikeToBeUpdated = bikesRepository.findById(id).get();
        updatedBike.setId(id);
        updatedBike.setOwner(bikeToBeUpdated.getOwner());
        bikesRepository.save(updatedBike);
    }

    @Transactional
    public void delete(int id) {
        bikesRepository.deleteById(id);
    }

    public Client getOwner(int id) {
        return bikesRepository.findById(id).map(Bike::getOwner).orElse(null);
    }

    @Transactional
    public void release(int id) {
        bikesRepository.findById(id).ifPresent(
                bike -> {
                    bike.setOwner(null);
                    bike.setTakenAt(null);
                });
    }

    @Transactional
    public void assign(int id, Client selectedClient, int hours) {
        bikesRepository.findById(id).ifPresent(
                bike -> {
                    bike.setOwner(selectedClient);
                    bike.setTakenAt(new Date());
                    Debt debt = new Debt();
                    debt.setOwner(selectedClient);
                    debt.setBike(bike);
                    debt.setAmount(bike.getPrice() * hours);
                    debtsRepository.save(debt);
                }
        );
    }

    @Transactional
    public Bike findById(int id) {
        Bike bike = bikesRepository.findById(id).get();
        return bike;
    }

}
