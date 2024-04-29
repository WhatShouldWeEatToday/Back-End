package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;
    private Long totalLikes;
    private int taste;
    private int cost;
    private int kind;
    private int mood;
    private int park;
    private Boolean certified;
    private String writer;

    public Review(ReviewRequestDTO requestDTO){
        this.taste = requestDTO.getTaste();
        this.cost = requestDTO.getCost();
        this.mood = requestDTO.getMood();
        this.kind = requestDTO.getKind();
        this.park = requestDTO.getPark();
        this.certified = requestDTO.getCertified();
        this.writer = requestDTO.getWriter();
    }


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
