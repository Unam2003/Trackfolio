package emanuelepiemonte.Trackfolio.services;

import emanuelepiemonte.Trackfolio.entities.GameStatus;
import emanuelepiemonte.Trackfolio.entities.Role;
import emanuelepiemonte.Trackfolio.entities.SavedGame;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.BadRequestException;
import emanuelepiemonte.Trackfolio.exceptions.NotFoundException;
import emanuelepiemonte.Trackfolio.exceptions.UnauthorizedException;
import emanuelepiemonte.Trackfolio.payload.SavedGameDTO;
import emanuelepiemonte.Trackfolio.repositories.SavedGameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SavedGameService {
    private final SavedGameRepository savedGameRepository;

    public SavedGameService(SavedGameRepository savedGameRepository) {
        this.savedGameRepository = savedGameRepository;
    }

    public SavedGame addToTrackfolio(User currentUser, SavedGameDTO body) {
        Optional<SavedGame> found = this.savedGameRepository.findByUserAndRawgId(currentUser, body.rawgId());

        if (found.isPresent()) {
            throw new BadRequestException("Attenzione!! Il gioco " + body.title() + " è già nella tua gamelist");
        }

        SavedGame newGame = new SavedGame();
        newGame.setTitle(body.title());
        newGame.setRawgId(body.rawgId());
        newGame.setCoverUrl(body.coverUrl());
        newGame.setPlatform(body.platform());
        newGame.setUser(currentUser);

        newGame.setStatus(body.status() != null ? body.status() : GameStatus.IN_LIST);
        newGame.setRating(body.rating());
        newGame.setHoursPlayed(body.hoursPlayed() != 0 ? body.hoursPlayed() : 0);

        return this.savedGameRepository.save(newGame);
    }

    public SavedGame updateSavedGame(User currentUser, UUID savedGameId, SavedGameDTO body) {
        SavedGame found = this.savedGameRepository.findById(savedGameId).orElseThrow(() -> new NotFoundException("Gioco non trovato"));

        if (!found.getUser().getUserId().equals(currentUser.getUserId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Non puoi modificare i giochi degli altri!");
        }

        if (body.status() != null) found.setStatus(body.status());
        if (body.rating() != 0) found.setRating(body.rating());
        if (body.hoursPlayed() != 0) found.setHoursPlayed(body.hoursPlayed());
        if (body.platform() != null) found.setPlatform(body.platform());

        return this.savedGameRepository.save(found);
    }

    public List<SavedGame> getMyGames(User currentUser) {
        return this.savedGameRepository.findByUser(currentUser);
    }

    public void removeFromTrackfolio(User currentUser, UUID savedGameId) {
        SavedGame found = this.savedGameRepository.findById(savedGameId).orElseThrow(() -> new NotFoundException("Gioco con id " + savedGameId + " non trovato nel DB"));

        // controllo utente e ruolo loggato nel proprio salvataggio
        if (!found.getUser().getUserId().equals(currentUser.getUserId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Bello, non puoi mica cancellare i giochi degli altri!");
        }
        this.savedGameRepository.delete(found);
    }

    public Integer getTotalHours(User user) {
        Integer total = this.savedGameRepository.sumTotalHoursByUser(user);
        return total != null ? total : 0;
    }

    public Optional<SavedGame> findByUserAndRawId(User currentUser, Long rawgId) {
        return this.savedGameRepository.findByUserAndRawgId(currentUser, rawgId);
    }


}
