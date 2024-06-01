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
    private Long voteCount1;
    private Long voteCount2;

    @OneToOne(mappedBy = "vote", fetch = FetchType.LAZY)
    private Chat chat;

    @Builder

    public Vote(String menu1, String menu2, Long voteCount1, Long voteCount2) {
        this.menu1 = menu1;
        this.menu2 = menu2;
        this.voteCount1 = voteCount1;
        this.voteCount2 = voteCount2;
    }

    public static Vote createVote(String menu1, String menu2) {
        return Vote.builder()
                .menu1(menu1)
                .menu2(menu2)
                .build();
    }

    public void setMenu1(String menu1) {
        this.menu1 = menu1;
    }

    public void setMenu2(String menu2) {
        this.menu2 = menu2;
    }

    public void incrementVoteCount1(Long voteCount1) {
        this.voteCount1 = voteCount1;
    }

    public void incrementVoteCount2(Long voteCount2) {
        this.voteCount2 = voteCount2;
    }
}
