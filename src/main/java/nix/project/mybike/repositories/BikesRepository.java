package nix.project.mybike.repositories;

import nix.project.mybike.models.Bike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BikesRepository extends JpaRepository<Bike, Integer> {


    List<Bike> findByTitleStartingWith(String title);
    Page<Bike> findByTitleStartingWith(String title, Pageable pageable);
    @Query(value = "SELECT * FROM Bike b WHERE b.title = ?1 AND b.taken_at IS NULL", nativeQuery = true)
    Page<Bike> findBikeAvailableAndByTitleLike(String title, Pageable pageable);

    @Query(value = "SELECT * FROM Bike b WHERE b.taken_at IS NULL", nativeQuery = true)
    Page<Bike> findBikeAvailable(Pageable pageable);

}
