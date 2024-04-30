package kit.project.whatshouldweeattoday.domain.dto.review;

import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequestDTO {
    private long restaurant_id;
    private String writer;
    private Long totalLikes;
    private int taste;
    private int mood;
    private int park;
    private int kind;
    private int cost;
    private Boolean certified;
    private double stars;

    //리뷰 등록
    @Builder
    public ReviewRequestDTO(String writer, int taste, int mood, int park, int kind, int cost, boolean certified,double stars, Long totalLikes) {
        this.writer=writer;
        this.taste = taste;
        this.mood = mood;
        this.park = park;
        this.kind = kind;
        this.cost = cost;
        this.certified = certified;
        this.stars = stars;
        this.totalLikes = totalLikes;
    }

}
