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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public SavedMedia(String title, Long tmdbId, String posterPath, MediaType type, User user) {
        this.title = title;
        this.tmdbId = tmdbId;
        this.posterPath = posterPath;
        this.type = type;
        this.user = user;
    }
}
