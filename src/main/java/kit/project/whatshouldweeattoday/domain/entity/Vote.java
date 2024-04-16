package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue
    @Column(name = "VOTE_ID")
    private Long id;
    private String menu;
    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;
}
