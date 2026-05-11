package emanuelepiemonte.Trackfolio.payload;

public record MovieRespDTO(Long id,
                           String title,
                           String name,
                           String poster_path,
                           Double vote_average,
                           String overview) {
}


