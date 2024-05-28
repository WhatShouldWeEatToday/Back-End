package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOTE_ID")
    private Long id;
    private String menu;
    private Integer voteCount = 0;

    @OneToOne(mappedBy = "vote", fetch = FetchType.LAZY)
    private Chat chat;

    @Builder
    public Vote(String menu) {
        this.menu = menu;
    }

    public static Vote createVote(String menu) {
        return Vote.builder()
                .menu(menu)
                .build();
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }
}
