package nix.project.mybike.services;

import nix.project.mybike.models.Client;
import nix.project.mybike.repositories.ClientsRepository;
import nix.project.mybike.secirity.ClientDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ClientDetailsService implements UserDetailsService {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientDetailsService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Client> user = clientsRepository.findByLogin(username);
        if(user.isPresent()) {
            return new ClientDetails(user.get());
        }
        throw new UsernameNotFoundException("User not found");
    }
}
