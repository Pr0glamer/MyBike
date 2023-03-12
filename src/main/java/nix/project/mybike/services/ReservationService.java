package nix.project.mybike.services;

import lombok.extern.slf4j.Slf4j;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.models.Reservation;
import nix.project.mybike.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ClientService clientService;

    private final EmailService emailService;

    @Value("${constant.email.info.manager}")
    private String manager;

    @Value("${constant.email.info.subject}")
    private String subject;


    private final ResourceLoader resourceLoader;


    @Autowired
    public ReservationService(ReservationRepository reservationRepository, ClientService clientService, EmailService emailService, ResourceLoader resourceLoader) {
        this.reservationRepository = reservationRepository;
        this.clientService = clientService;
        this.emailService = emailService;
        this.resourceLoader = resourceLoader;
    }


    public Reservation findOne(int id) {
        Optional<Reservation> foundDebt = reservationRepository.findById(id);
        return foundDebt.orElse(null);
    }
    private String emailTemplate() {
        Resource resource = resourceLoader.getResource("classpath:email-template.txt");
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    @Transactional
    public void reserve(Bike bike, Date date) {
        String template = emailTemplate();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Client> client = clientService.getUserByLogin(auth.getName());
        if(client.isPresent()) {
            String message = String.format(template, bike.toString(), date.toString(), client.get().toString(), client.get().getTelephone());
            Reservation reservation = new Reservation();
            reservation.setReserveDate(date);
            reservation.setReservedBike(bike);
            reservation.setClientWhoReserved(client.get());
            reservationRepository.save(reservation);
            emailService.sendSimpleMessage(manager,
                    subject,
                    message);
        }
    }

}
