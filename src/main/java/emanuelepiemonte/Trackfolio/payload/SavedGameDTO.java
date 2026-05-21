package emanuelepiemonte.Trackfolio.payload;

import emanuelepiemonte.Trackfolio.entities.GameStatus;
import emanuelepiemonte.Trackfolio.entities.Platform;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SavedGameDTO(
        @NotNull(message = "L'id di RAWG è obbligatorio!")
        Long rawgId,

        @NotEmpty(message = "Il titolo è obbligatorio!")
        String title,

        String coverUrl,

        @NotNull(message = "La piattaforma (PLAYSTATION_5, PLAYSTATION_4, XBOX_SERIES_X, XBOX_ONE, PC, NINTENDO_SWITCH, MOBILE, RETRO_CONSOLE) è obbligatoria!")
        Platform platform,

        @NotNull(message = "Lo stato (IN_LIST, PLAYING, COMPLETED, PLATINUM, DROPPED) è obbligatorio!")
        GameStatus status,

        @Min(0) @Max(5)
        Double rating,

        @Min(0)
        int hoursPlayed
) {
}
