package emanuelepiemonte.Trackfolio.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GameRespDTO(
        Long id,
        String name,
        @JsonProperty("background_image")
        String backgroundImage,
        @JsonProperty("released")
        String releaseDate,
        double rating,
        String description,
        int playtime,
        Integer metacritic,
        List<GenreDTO> genres
) {
    public record GenreDTO(
            Long id,
            String name,
            String slug
    ) {
    }

}
