package emanuelepiemonte.Trackfolio.payload;

import emanuelepiemonte.Trackfolio.entities.MediaType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SavedMediaDTO(
        @NotEmpty(message = "Il titolo è obbligatorio!")
        String title,

        @NotNull(message = "L'ID di TMDB è obbligatorio!")
        Long tmdbId,

        String posterPath,

        @NotNull(message = "Il tipo (MOVIE, TV_SERIES, ANIME) è obbligatorio!")
        MediaType type
) {
}
