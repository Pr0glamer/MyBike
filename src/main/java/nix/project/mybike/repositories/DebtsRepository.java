package nix.project.mybike.repositories;

import nix.project.mybike.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DebtsRepository extends JpaRepository<Debt, Integer> {
    @Query(value = "SELECT SUM(amount) FROM client_debt WHERE client_id=?1", nativeQuery = true)
    int totalDebt(int client_id);

}
