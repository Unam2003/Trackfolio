package emanuelepiemonte.Trackfolio.services;

import emanuelepiemonte.Trackfolio.entities.MediaStatus;
import emanuelepiemonte.Trackfolio.entities.Role;
import emanuelepiemonte.Trackfolio.entities.SavedMedia;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.BadRequestException;
import emanuelepiemonte.Trackfolio.exceptions.NotFoundException;
import emanuelepiemonte.Trackfolio.exceptions.UnauthorizedException;
import emanuelepiemonte.Trackfolio.payload.SavedMediaDTO;
import emanuelepiemonte.Trackfolio.repositories.SavedMediaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SavedMediaService {
    private SavedMediaRepository savedMediaRepository;

    public SavedMediaService(SavedMediaRepository savedMediaRepository) {
        this.savedMediaRepository = savedMediaRepository;
    }

    public SavedMedia addToTrackfolio(User currentUser, SavedMediaDTO body) {
        // controllo se l'utente salva più volte lo stesso media
        Optional<SavedMedia> found = this.savedMediaRepository.findByUserAndTmdbId(currentUser, body.tmdbId());

        if (found.isPresent()) {
            throw new BadRequestException("Attenzione!! Il media " + body.title() + " è già nella tua watchlist");
        }

        SavedMedia newMedia = new SavedMedia();
        newMedia.setTitle(body.title());
        newMedia.setTmdbId(body.tmdbId());
        newMedia.setPosterPath(body.posterPath());
        newMedia.setType(body.type());
        newMedia.setUser(currentUser);

        newMedia.setStatus(body.status() != null ? body.status() : MediaStatus.PLAN_TO_WATCH);
        newMedia.setRating(body.rating() != 0 ? body.rating() : 0);
        newMedia.setLastEpisodeWatched(body.lastEpisodeWatched() != 0 ? body.lastEpisodeWatched() : 0);
        newMedia.setLastSeasonWatched(body.lastSeasonWatched() != 0 ? body.lastSeasonWatched() : 1);

        return this.savedMediaRepository.save(newMedia);
    }

    public SavedMedia updateSavedMedia(User currentUser, UUID savedMediaId, SavedMediaDTO body) {
        SavedMedia found = this.savedMediaRepository.findById(savedMediaId).orElseThrow(() -> new NotFoundException("Media non trovato"));

        if (!found.getUser().getUserId().equals(currentUser.getUserId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Non puoi modificare i media degli altri!");
        }

        if (body.status() != null) found.setStatus(body.status());
        if (body.rating() != 0) found.setRating(body.rating());
        if (body.lastEpisodeWatched() != 0) found.setLastEpisodeWatched(body.lastEpisodeWatched());
        if (body.lastSeasonWatched() != 0) found.setLastSeasonWatched(body.lastSeasonWatched());

        return this.savedMediaRepository.save(found);
    }

    public List<SavedMedia> getMyTrackfolio(User currentUser) {
        return this.savedMediaRepository.findByUser(currentUser);
    }

    public void removeFromTrackfolio(User currentUser, UUID savedMediaId) {
        SavedMedia found = this.savedMediaRepository.findById(savedMediaId).orElseThrow(() -> new NotFoundException("Media con id " + savedMediaId + " non trovato nel DB"));

        // controllo utente e ruolo loggato nel proprio salvataggio
        if (!found.getUser().getUserId().equals(currentUser.getUserId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Bello, non puoi mica cancellare i media degli altri!");
        }
        this.savedMediaRepository.delete(found);
    }


}
