package emanuelepiemonte.Trackfolio.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TmdbService {

    private final RestTemplate restTemplate;
    @Value("${tmdb.api.key}")
    private String apiKey;

    public TmdbService() {
        this.restTemplate = new RestTemplate();
    }

    // RICERCA PER TITOLO
    public Object searchMovies(String query) {
        String url = "https://api.themoviedb.org/3/search/multi?api_key=" + apiKey + "&query=" + query + "&language=it-IT";
        return restTemplate.getForObject(url, Object.class);
    }

    // TREND DEL MOMENTO (per la home)
    public Object getTrending() {
        String url = "https://api.themoviedb.org/3/trending/all/day?api_key=" + apiKey + "&language=it-IT";
        return restTemplate.getForObject(url, Object.class);
    }
}
