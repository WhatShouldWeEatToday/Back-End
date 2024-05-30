package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOTE_ID")
    private Long id;

    private String menu1;
    private String menu2;
    private Integer voteCount = 0;

    @OneToOne(mappedBy = "vote", fetch = FetchType.LAZY)
    private Chat chat;

    @Builder
    public Vote(String menu1, String menu2) {
        this.menu1 = menu1;
        this.menu2 = menu2;
    }

    public static Vote createVote(String menu1, String menu2) {
        return Vote.builder()
                .menu1(menu1)
                .menu2(menu2)
                .build();
    }

    public void incrementVoteCount() {
        this.voteCount++;
    }
}
