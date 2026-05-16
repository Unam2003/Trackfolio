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

    @GetMapping("/{id}")
    public GameRespDTO getGameDetails(@PathVariable int id) {
        return externalApiService.getGameDetails(id);
    }

    @GetMapping
    public List<GameRespDTO> getGamesList(@RequestParam(defaultValue = "1") int page) {
        return externalApiService.getAllGames(page);
    }
}
