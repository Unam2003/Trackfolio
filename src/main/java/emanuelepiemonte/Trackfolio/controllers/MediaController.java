package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.entities.SavedMedia;
import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.payload.MovieRespDTO;
import emanuelepiemonte.Trackfolio.services.ExternalApiService;
import emanuelepiemonte.Trackfolio.services.SavedMediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class MediaController {
    private final ExternalApiService externalApiService;
    private final SavedMediaService savedMediaService;

    public MediaController(ExternalApiService externalApiService, SavedMediaService savedMediaService) {
        this.externalApiService = externalApiService;
        this.savedMediaService = savedMediaService;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieRespDTO>> getMovies(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(externalApiService.fetchMovies(page));
    }

    @GetMapping("/movies/search")
    public ResponseEntity<List<MovieRespDTO>> searchMovies(@RequestParam String query) {
        return ResponseEntity.ok(externalApiService.searchMovies(query));
    }

    @GetMapping("/tv_series")
    public ResponseEntity<List<MovieRespDTO>> getTvSeries(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(externalApiService.fetchTvSeries(page));
    }

    @GetMapping("/tv_series/search")
    public ResponseEntity<List<MovieRespDTO>> searchTvAndAnime(@RequestParam String query) {
        return ResponseEntity.ok(externalApiService.searchTvAndAnime(query));
    }

    @GetMapping("/anime")
    public ResponseEntity<List<MovieRespDTO>> getAnime(@RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(externalApiService.fetchAnime(page));
    }

    @GetMapping("/movies/details")
    public ResponseEntity<Object> getMovieDetails(@RequestParam int id) {
        return ResponseEntity.ok(externalApiService.fetchMovieDetails(id));
    }

    @GetMapping("/tv_series/details")
    public ResponseEntity<Object> getTvDetails(@RequestParam int id) {
        return ResponseEntity.ok(externalApiService.fetchTvSeriesDetails(id));
    }

    @GetMapping("/tv_series/season")
    public ResponseEntity<Object> getSeasonDetails(@RequestParam int id, @RequestParam int seasonNumber) {
        return ResponseEntity.ok(externalApiService.fetchSeasonDetails(id, seasonNumber));
    }

    @PutMapping("/tv_series/watch")
    public ResponseEntity<SavedMedia> watchEpisode(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long tmdbId,
            @RequestParam int season,
            @RequestParam int episode) {

        return ResponseEntity.ok(this.savedMediaService.updateLastWatchedEpisode(currentUser, tmdbId, season, episode));
    }


}
