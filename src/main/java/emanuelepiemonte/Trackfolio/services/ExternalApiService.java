package emanuelepiemonte.Trackfolio.services;

import emanuelepiemonte.Trackfolio.payload.MovieRespDTO;
import emanuelepiemonte.Trackfolio.payload.TmdbResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ExternalApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${TMDB_URL}")
    private String tmdbUrl;

    @Value("${TMDB_TOKEN}")
    private String tmdbToken;

    public List<MovieRespDTO> fetchMovies() {
        return makeApiCall(tmdbUrl + "/movie/popular?language=it-IT");
    }

    public List<MovieRespDTO> fetchTvSeries() {
        return makeApiCall(tmdbUrl + "/tv/popular?language=it-IT");
    }

    // gli anime sono serie tv, però voglio una fetch a parte e nella documentazione ho visto che c'è il discovery
    public List<MovieRespDTO> fetchAnime() {
        return makeApiCall(tmdbUrl + "/discover/tv?language=it-IT&with_origin_country=JP&with_genres=16"); // 16 = genere animazione in TMDB
    }

    private List<MovieRespDTO> makeApiCall(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<TmdbResponseDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, TmdbResponseDTO.class);

        return response.getBody().results();
    }
}
