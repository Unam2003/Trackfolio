package emanuelepiemonte.Trackfolio.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "saved_game")
public class SavedGame {
    @Column(nullable = false)
    String title;
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "saved_game_id")
    private UUID savedGameId;
    @Column(name = "rawg_id", nullable = false)
    private Long rawgId;

    @Column(name = "cover_url")
    private String coverUrl;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private int rating;

    @Column(name = "hours_played")
    private int hoursPlayed;

    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public SavedGame(String title, Long rawgId, String coverUrl, Platform platform, User user) {
        this.title = title;
        this.rawgId = rawgId;
        this.coverUrl = coverUrl;
        this.platform = platform;
        this.user = user;
        this.status = GameStatus.IN_LIST;
        this.rating = 0;
        this.hoursPlayed = 0;
    }
}
