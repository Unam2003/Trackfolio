package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.payload.GameRespDTO;
import emanuelepiemonte.Trackfolio.services.ExternalApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {
    private final ExternalApiService externalApiService;

    public GameController(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
    }

    @GetMapping("/search")
    public List<GameRespDTO> getGames(@RequestParam String query) {
        return externalApiService.fetchGames(query);
    }

    @GetMapping
    public List<GameRespDTO> getGamesList(@RequestParam(defaultValue = "1") int page) {
        return externalApiService.getAllGames(page);
    }

    @GetMapping("/trending")
    public List<GameRespDTO> getTrenging() {
        return externalApiService.getTrendingGames();
    }

    @GetMapping("/upcoming")
    public List<GameRespDTO> getUpcoming() {
        return externalApiService.fetchUpcomingGames();
    }

    @GetMapping("/genre/{genreId}")
    public List<GameRespDTO> getByGenre(@PathVariable String genreId) {
        return externalApiService.fetchGamesByGenre(genreId);
    }

    @GetMapping("/{id}")
    public GameRespDTO getGameDetails(@PathVariable int id) {
        return externalApiService.getGameDetails(id);
    }


}
