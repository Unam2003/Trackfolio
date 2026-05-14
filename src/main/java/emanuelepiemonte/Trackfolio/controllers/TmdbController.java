package emanuelepiemonte.Trackfolio.controllers;

import emanuelepiemonte.Trackfolio.services.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class TmdbController {
    private TmdbService tmdbService;

    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/search")
    public Object search(@RequestParam String query) {
        return tmdbService.searchMovies(query);
    }

    @GetMapping("/trending")
    public Object trending() {
        return tmdbService.getTrending();
    }

}
