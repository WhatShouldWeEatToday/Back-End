package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private double stars;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_id")
    private Image image;

    @OneToMany(mappedBy = "review")
    private List<Likes> likesList;

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    public Review(ReviewRequestDTO requestDTO){
        this.taste = requestDTO.getTaste();
        this.cost = requestDTO.getCost();
        this.mood = requestDTO.getMood();
        this.kind = requestDTO.getKind();
        this.park = requestDTO.getPark();
        this.certified = requestDTO.getCertified();
        this.writer = requestDTO.getWriter();
        this.stars = requestDTO.getStars();
        this.totalLikes = requestDTO.getTotalLikes();
    }

    //리뷰수정
    public void updateReview(int cost, int park, int mood, int kind, int taste, double stars) {
        this.cost = cost;
        this.park = park;
        this.mood= mood;
        this.kind = kind;
        this.taste = taste;
        this.stars = stars;
    }


}
