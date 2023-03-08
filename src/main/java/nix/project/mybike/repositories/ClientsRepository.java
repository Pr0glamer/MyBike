package nix.project.mybike.repositories;

import nix.project.mybike.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ClientsRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByFullName(String fullName);

    Optional<Client> findByLogin(String login);
}
