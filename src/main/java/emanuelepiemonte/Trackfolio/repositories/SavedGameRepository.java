package emanuelepiemonte.Trackfolio.repositories;

import emanuelepiemonte.Trackfolio.entities.SavedGame;
import emanuelepiemonte.Trackfolio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedGameRepository extends JpaRepository<SavedGame, UUID> {
    List<SavedGame> findByUser(User user);

    Optional<SavedGame> findByUserAndRawgId(User user, Long rawgId);

    @Query("SELECT SUM(g.hoursPlayed) FROM SavedGame g Where g.user = :user")
    Integer sumTotalHoursByUser(@Param("user") User user);
}
