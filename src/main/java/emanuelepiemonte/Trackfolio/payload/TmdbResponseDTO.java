package emanuelepiemonte.Trackfolio.payload;

import java.util.List;

public record TmdbResponseDTO(List<MovieRespDTO> results) {
}
