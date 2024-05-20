package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VOTE_ID")
    private Long id;
    private String menu;
    private Long voteCount;

    @OneToOne(mappedBy = "vote", fetch = FetchType.LAZY)
    private Meet meet;

    public void incrementVoteCount() {
        this.voteCount++;
    }
}
