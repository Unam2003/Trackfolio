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
    private final ExternalApiService externalApiService;
    private final SavedMediaRepository savedMediaRepository;

    public SavedMediaService(SavedMediaRepository savedMediaRepository, ExternalApiService externalApiService) {
        this.savedMediaRepository = savedMediaRepository;
        this.externalApiService = externalApiService;
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

    public SavedMedia updateLastWatchedEpisode(User currentUser, Long tmdbId, int season, int episode) {
        SavedMedia found = this.savedMediaRepository.findByUserAndTmdbId(currentUser, tmdbId)
                .orElseThrow(() -> new NotFoundException("Questo contenuto non è presente nella tua watchlist. Salvalo prima!"));

        // --- 1. LOGICA PER ANDARE ALL'INDIETRO (Deselezione episodio) ---
        if (found.getLastSeasonWatched() == season && found.getLastEpisodeWatched() == episode) {
            if (episode > 1) {
                found.setLastEpisodeWatched(episode - 1);
            } else if (season > 1) {
                // Recuperiamo la lunghezza della stagione precedente per impostare l'ultimo episodio di quella stagione
                int previousSeason = season - 1;
                int lastEpOfPreviousSeason = getEpisodesCountFromTmdb(tmdbId, previousSeason);
                found.setLastEpisodeWatched(lastEpOfPreviousSeason);
                found.setLastSeasonWatched(previousSeason);
            } else {
                found.setLastEpisodeWatched(0);
                found.setLastSeasonWatched(1);
            }
            return this.savedMediaRepository.save(found);
        }

        // --- 2. LOGICA PER ANDARE AVANTI (Incremento) ---

        // Recuperiamo il numero reale di episodi di questa stagione tramite l'ExternalApiService
        int totalEpisodesInCurrentSeason = getEpisodesCountFromTmdb(tmdbId, season);

        if (episode > totalEpisodesInCurrentSeason) {
            // Se l'episodio richiesto supera la stagione, verifichiamo quante stagioni esistono in totale
            int totalSeasonsInSeries = getTotalSeasonsFromTmdb(tmdbId);

            if (season < totalSeasonsInSeries) {
                // C'è una stagione successiva: passiamo automaticamente a S(corrente + 1) ed Episodio 1
                found.setLastSeasonWatched(season + 1);
                found.setLastEpisodeWatched(1);
            } else {
                // È l'ultima stagione: blocchiamo l'avanzamento all'ultimo episodio reale e completiamo il tracking
                found.setLastEpisodeWatched(totalEpisodesInCurrentSeason);
                found.setStatus(MediaStatus.COMPLETED);
            }
        } else {
            // L'episodio ci sta dentro: aggiorniamo normalmente
            found.setLastSeasonWatched(season);
            found.setLastEpisodeWatched(episode);
        }

        // Aggiorna lo stato se era ancora da iniziare
        if (found.getStatus() == MediaStatus.PLAN_TO_WATCH) {
            found.setStatus(MediaStatus.WATCHING);
        }

        return this.savedMediaRepository.save(found);
    }

    public SavedMedia updateStatus(User currentUser, UUID savedMediaId, String status) {
        SavedMedia found = this.savedMediaRepository.findById(savedMediaId)
                .orElseThrow(() -> new NotFoundException("Media non trovato"));

        if (!found.getUser().getUserId().equals(currentUser.getUserId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Non puoi modificare i media degli altri!");
        }

        // Converti la stringa del frontend nell'Enum MediaStatus
        try {
            found.setStatus(MediaStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Stato non valido: " + status);
        }

        return this.savedMediaRepository.save(found);
    }


    private int getEpisodesCountFromTmdb(Long tmdbId, int seasonNumber) {
        try {
            Object seasonData = this.externalApiService.fetchSeasonDetails(tmdbId.intValue(), seasonNumber);
            if (seasonData instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) seasonData;
                java.util.List<?> episodesList = (java.util.List<?>) map.get("episodes");
                if (episodesList != null) {
                    return episodesList.size();
                }
            }
        } catch (Exception e) {
            System.err.println("Errore recupero episodi: " + e.getMessage());
        }
        return 10;
    }

    private int getTotalSeasonsFromTmdb(Long tmdbId) {
        try {
            Object tvDetails = this.externalApiService.fetchTvSeriesDetails(tmdbId.intValue());
            if (tvDetails instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) tvDetails;
                Number totalSeasonsNum = (Number) map.get("number_of_seasons");
                if (totalSeasonsNum != null) {
                    return totalSeasonsNum.intValue();
                }
            }
        } catch (Exception e) {
            System.err.println("Errore recupero stagioni: " + e.getMessage());
        }
        return 1;
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

    public Optional<SavedMedia> findByTmdbIdAndUser(User currentUser, Long tmdbId) {
        return this.savedMediaRepository.findByUserAndTmdbId(currentUser, tmdbId);
    }


}
