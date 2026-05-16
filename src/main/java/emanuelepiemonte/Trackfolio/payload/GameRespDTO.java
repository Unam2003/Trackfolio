package emanuelepiemonte.Trackfolio.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GameRespDTO(
        Long id,
        String name,
        @JsonProperty("background_image")
        String backgroundImage,
        @JsonProperty("released")
        String releaseDate,
        double rating,
        String description
) {
}
