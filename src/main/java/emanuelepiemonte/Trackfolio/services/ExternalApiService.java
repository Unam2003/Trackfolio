package emanuelepiemonte.Trackfolio.services;

import emanuelepiemonte.Trackfolio.payload.GameRespDTO;
import emanuelepiemonte.Trackfolio.payload.MovieRespDTO;
import emanuelepiemonte.Trackfolio.payload.RawgResponseDTO;
import emanuelepiemonte.Trackfolio.payload.TmdbResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
public class ExternalApiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${TMDB_URL}")
    private String tmdbUrl;

    @Value("${TMDB_TOKEN}")
    private String tmdbToken;

    @Value("${RAWG_KEY}")
    private String rawgKey;

    // ----------------> METODI DI TMDB <-----------------

    public List<MovieRespDTO> fetchMovies(int page) {
        List<MovieRespDTO> p1 = tmdbApiCall(tmdbUrl + "/discover/movie?language=it-IT&include_adult=false&sort_by=vote_count.desc&page=" + page);
        List<MovieRespDTO> p2 = tmdbApiCall(tmdbUrl + "/discover/movie?language=it-IT&include_adult=false&sort_by=vote_count.desc&page=" + (page + 1));

        List<MovieRespDTO> total = new ArrayList<>();
        if (p1 != null) total.addAll(p1);
        if (p2 != null) total.addAll(p2);
        return total;
    }

    public List<MovieRespDTO> searchMovies(String query) {
        return tmdbApiCall(tmdbUrl + "/search/movie?language=it-IT&query=" + query);
    }

    public List<MovieRespDTO> fetchTvSeries(int page) {
        List<MovieRespDTO> p1 = tmdbApiCall(tmdbUrl + "/discover/tv?language=it-IT&include_adult=false&sort_by=vote_count.desc&without_genres=16&page=" + page);
        List<MovieRespDTO> p2 = tmdbApiCall(tmdbUrl + "/discover/tv?language=it-IT&include_adult=false&sort_by=vote_count.desc&without_genres=16&page=" + (page + 1));

        List<MovieRespDTO> total = new ArrayList<>();
        if (p1 != null) total.addAll(p1);
        if (p2 != null) total.addAll(p2);
        return total;
    }

    public List<MovieRespDTO> searchTvAndAnime(String query) {
        return tmdbApiCall(tmdbUrl + "/search/tv?language=it-IT&query=" + query);
    }

    // gli anime sono serie tv, però voglio una fetch a parte e nella documentazione ho visto che c'è il discovery
    public List<MovieRespDTO> fetchAnime(int page) {
        List<MovieRespDTO> p1 = tmdbApiCall(tmdbUrl + "/discover/tv?language=it-IT&with_origin_country=JP&with_genres=16&include_adult=false&sort_by=vote_count.desc&page=" + page);
        List<MovieRespDTO> p2 = tmdbApiCall(tmdbUrl + "/discover/tv?language=it-IT&with_origin_country=JP&with_genres=16&include_adult=false&sort_by=vote_count.desc&page=" + (page + 1));

        List<MovieRespDTO> total = new ArrayList<>();
        if (p1 != null) total.addAll(p1);
        if (p2 != null) total.addAll(p2);
        return total;
    }

    private List<MovieRespDTO> tmdbApiCall(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("Accept", "application/json");
        headers.set("Accept-Encoding", "gzip, identity");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        try {
            byte[] body = response.getBody();
            InputStream inputStream = new ByteArrayInputStream(body);

            // DECOMPRESSIONE GZIP FASTIDIOSISSIMA
            if (body.length > 2 && body[0] == 0x1f && body[1] == (byte) 0x8b) {
                inputStream = new GZIPInputStream(inputStream);
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TmdbResponseDTO dto = mapper.readValue(inputStream, TmdbResponseDTO.class);
            return dto.results();

        } catch (Exception e) {
            throw new RuntimeException("Errore di decompressione o parsing: " + e.getMessage(), e);
        }
    }

    public Object fetchMovieDetails(int id) {
        String url = tmdbUrl + "/movie/" + id + "?language=it-IT&append_to_response=credits";
        return callGenericTmdb(url);
    }

    public Object fetchTvSeriesDetails(int id) {
        String url = tmdbUrl + "/tv/" + id + "?language=it-IT&append_to_response=credits";
        return callGenericTmdb(url);
    }

    private Object callGenericTmdb(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        return response.getBody();
    }

    public Object fetchSeasonDetails(int seriesId, int seasonNumber) {
        String url = tmdbUrl + "/tv/" + seriesId + "/season/" + seasonNumber + "?language=it-IT";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        return response.getBody();
    }


    // ----------------> METODI DI RAWG <-----------------

    public List<GameRespDTO> fetchGames(String query) {
        String url = "https://api.rawg.io/api/games?key=" + rawgKey + "&search=" + query + "&language=it";
        ResponseEntity<RawgResponseDTO> response = restTemplate.getForEntity(url, RawgResponseDTO.class);
        return response.getBody().results();
    }

    public List<GameRespDTO> getTrendingGames() {
        String url = "https://api.rawg.io/api/games?key=" + rawgKey + "&dates=2025-01-01,2026-10-31&ordering=-added&language=it";
        RawgResponseDTO response = restTemplate.getForObject(url, RawgResponseDTO.class);
        return (response != null) ? response.results() : new ArrayList<>();
    }

    public List<GameRespDTO> getAllGames(int page) {
        String url = "https://api.rawg.io/api/games?key=" + rawgKey + "&page=" + page + "&language=it";
        RawgResponseDTO response = restTemplate.getForObject(url, RawgResponseDTO.class);
        if (response == null) {
            return new ArrayList<>();
        }
        return response.results();
    }


    public List<GameRespDTO> fetchUpcomingGames() {
        String url = "https://api.rawg.io/api/games?key=" + rawgKey + "&dates=2026-05-20,2027-05-20&ordering=-released&page_size=20&language=it";
        RawgResponseDTO response = restTemplate.getForObject(url, RawgResponseDTO.class);
        return response.results().stream()
                .skip(9) // Skip perché le prime 9 sono giochi strani
                .limit(20) // Limitiamo a 20 per mantenere il carosello pulito
                .toList();
    }

    public List<GameRespDTO> fetchGamesByGenre(String genreId) {
        String url = "https://api.rawg.io/api/games?key=" + rawgKey + "&genres=" + genreId + "&ordering=-added&page_size=20&language=it";
        RawgResponseDTO response = restTemplate.getForObject(url, RawgResponseDTO.class);
        return (response != null) ? response.results() : new ArrayList<>();
    }

    public GameRespDTO getGameDetails(int gameId) {
        String url = "https://api.rawg.io/api/games/" + gameId + "?key=" + rawgKey + "&language=it";
        return restTemplate.getForObject(url, GameRespDTO.class);
    }

    public Object fetchActorDetails(int id) {
        String url = tmdbUrl + "/person/" + id + "?language=it-IT&append_to_response=combined_credits";
        Object response = callGenericTmdb(url);

        if (response instanceof java.util.Map) {
            java.util.Map actorMap = (java.util.Map) response;
            String bio = (String) actorMap.get("biography");

            if (bio == null || bio.equals("")) {
                String urlEng = tmdbUrl + "/person/" + id + "?language=en-US";
                Object responseEng = callGenericTmdb(urlEng);

                if (responseEng instanceof java.util.Map) {
                    java.util.Map actorMapEng = (java.util.Map) responseEng;
                    actorMap.put("biography", actorMapEng.get("biography"));
                }
            }
        }

        return response;
    }


}
