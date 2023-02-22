package nix.project.mybike.controllers.rest;

import nix.project.mybike.models.Client;
import nix.project.mybike.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rest/clients")
public class ClientRESTController {

    private final ClientService clientService;

    @Autowired
    public ClientRESTController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable int id) {
        Client client = clientService.findOne(id);
        if (client != null) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {

        Client savedClient = clientService.save(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable int id, @Valid @RequestBody Client updatedClient) {
        Client client = clientService.findOne(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        client.setFullName(updatedClient.getFullName());
        client.setYearOfBirth(updatedClient.getYearOfBirth());
        client.setTelephone(updatedClient.getTelephone());
        Client savedClient = clientService.save(client);
        return ResponseEntity.ok(savedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable int id) {
        Client client = clientService.findOne(id);
        if (client != null) {
            clientService.delete(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}