package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "REVIEW_ID")
    private Long id;
    private Long totalLikes;
    private int taste;
    private int cost;
    private int kind;
    private int mood;
    private int park;
    private Boolean certified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "likes_id")
    private Likes likes;
}
