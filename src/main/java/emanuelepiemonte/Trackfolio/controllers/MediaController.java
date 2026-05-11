package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.payload.MovieRespDTO;
import emanuelepiemonte.Trackfolio.services.ExternalApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class MediaController {
    private final ExternalApiService externalApiService;

    public MediaController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/movies")
    public ResponseEntity<List<MovieRespDTO>> getMovies() {
        return ResponseEntity.ok(externalApiService.fetchMovies());
    }

    @GetMapping("/tv_series")
    public ResponseEntity<List<MovieRespDTO>> getTvSeries() {
        return ResponseEntity.ok(externalApiService.fetchTvSeries());
    }

    @GetMapping("/anime")
    public ResponseEntity<List<MovieRespDTO>> getAnime() {
        return ResponseEntity.ok(externalApiService.fetchAnime());
    }
}
