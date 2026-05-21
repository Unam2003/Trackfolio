package emanuelepiemonte.Trackfolio.payload;

import emanuelepiemonte.Trackfolio.entities.MediaStatus;
import emanuelepiemonte.Trackfolio.entities.MediaType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SavedMediaDTO(
        @NotEmpty(message = "Il titolo è obbligatorio!")
        String title,

        @NotNull(message = "L'ID di TMDB è obbligatorio!")
        Long tmdbId,

        String posterPath,

        @NotNull(message = "Il tipo (MOVIE, TV_SERIES, ANIME) è obbligatorio!")
        MediaType type,

        @NotNull(message = "Lo status (WATCHING, COMPLETED, DROPPED, PLAN_TO_WATCH) è obbligatorio!!")
        MediaStatus status,

        @Min(0) @Max(10)
        Integer rating,

        @Min(0)
        Integer lastEpisodeWatched,

        @Min(0)
        Integer lastSeasonWatched
) {
}
