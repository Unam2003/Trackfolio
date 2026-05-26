package emanuelepiemonte.Trackfolio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "saved_media")
public class SavedMedia {
    @Column(nullable = false)
    String title;
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "saved_media_id")
    private UUID savedMediaId;
    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(name = "poster_path")
    private String posterPath;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private int rating;

    @Enumerated(EnumType.STRING)
    private MediaStatus status;

    @Column(name = "last_episode_watched")
    private int lastEpisodeWatched;

    @Column(name = "last_season_watched")
    private int lastSeasonWatched;

    @Column(name = "run_time")
    private Integer runtime;

    @Column(name = "episode_run_time")
    private Integer episodeRunTime;

    @Column(name = "number_of_episodes")
    private Integer numberOfEpisodes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public SavedMedia(String title, Long tmdbId, String posterPath, MediaType type, int rating, MediaStatus status, int lastEpisodeWatched, int lastSeasonWatched, Integer runtime, Integer episodeRunTime, Integer numberOfEpisodes, User user) {
        this.title = title;
        this.tmdbId = tmdbId;
        this.posterPath = posterPath;
        this.type = type;
        this.rating = rating;
        this.status = status;
        this.lastEpisodeWatched = lastEpisodeWatched;
        this.lastSeasonWatched = lastSeasonWatched;
        this.runtime = runtime;
        this.episodeRunTime = episodeRunTime;
        this.numberOfEpisodes = numberOfEpisodes;
        this.user = user;
    }
}
