package emanuelepiemonte.Trackfolio.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieRespDTO(
        Long id,
        String title,
        String name,
        String poster_path,
        String backdrop_path,
        Double vote_average,
        Double popularity,
        String overview,
        String release_date,
        String first_air_date,
        List<Integer> genre_ids) {
}


