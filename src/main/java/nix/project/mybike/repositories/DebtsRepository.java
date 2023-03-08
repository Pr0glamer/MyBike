package nix.project.mybike.repositories;

import nix.project.mybike.models.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DebtsRepository extends JpaRepository<Debt, Integer> {
    @Query(value = "SELECT SUM(amount) FROM client_debt WHERE client_id=?1 AND bike_id=?2 HAVING SUM(amount) > 0", nativeQuery = true)
    int getTotalDebt(int client_id, int bike_id);

    @Query(value = "SELECT SUM(amount), client_id, bike_id FROM client_debt " +
            "GROUP BY client_id, bike_id HAVING SUM(amount) > 0", nativeQuery = true)
    List<Object[]> getAllDebts();

}
