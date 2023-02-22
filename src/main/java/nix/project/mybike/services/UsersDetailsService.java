package nix.project.mybike.services;

import nix.project.mybike.models.User;
import nix.project.mybike.repositories.UserRepository;
import nix.project.mybike.secirity.UsersDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByLogin(username);
        if(user.isPresent()) {
            return new UsersDetails(user.get());
        }
        throw new UsernameNotFoundException("User not found");
    }
}
