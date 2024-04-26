package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Likes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKES_ID")
    private Long id;
    private Boolean state;

    @OneToOne(mappedBy = "likes", fetch = FetchType.LAZY)
    private Review review;
}
