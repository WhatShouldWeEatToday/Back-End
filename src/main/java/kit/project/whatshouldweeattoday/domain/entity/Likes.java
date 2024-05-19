package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Likes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKES_ID")
    private Long id;
    private Boolean state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Likes(Boolean state){
        this.state=state;
    }
}
