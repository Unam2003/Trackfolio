package emanuelepiemonte.Trackfolio.services;

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

    public SavedMedia addToTrackfolio(User user, SavedMediaDTO body) {
        // controllo se l'utente salva più volte lo stesso media
        Optional<SavedMedia> found = this.savedMediaRepository.findByUserAndTmdbId(user, body.tmdbId());

        if (found.isPresent()) {
            throw new BadRequestException("Attenzione!! Il media " + body.title() + " è già nella tua watchlist");
        }

        SavedMedia newMedia = new SavedMedia(
                body.title(),
                body.tmdbId(),
                body.posterPath(),
                body.type(),
                user
        );

        return this.savedMediaRepository.save(newMedia);
    }

    public List<SavedMedia> getMyTrackfolio(User user) {
        return this.savedMediaRepository.findByUser(user);
    }

    public void removeFromTrackfolio(User user, UUID savedMediaId) {
        SavedMedia found = this.savedMediaRepository.findById(savedMediaId).orElseThrow(() -> new NotFoundException("Media con id " + savedMediaId + " non trovato nel DB"));

        // controllo utente loggato nel proprio salvataggio
        if (!found.getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedException("Bello, non puoi mica cancellare i media degli altri!");
        }
        this.savedMediaRepository.delete(found);
    }

}
