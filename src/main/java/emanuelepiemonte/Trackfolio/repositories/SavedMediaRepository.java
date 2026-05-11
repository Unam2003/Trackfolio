package emanuelepiemonte.Trackfolio.repositories;

import emanuelepiemonte.Trackfolio.entities.SavedMedia;
import emanuelepiemonte.Trackfolio.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavedMediaRepository extends JpaRepository<SavedMedia, UUID> {
    List<SavedMedia> findByUser(User user);

    Optional<SavedMedia> findByUserAndTmdbId(User user, Long tmdbId);
}
