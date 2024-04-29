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
    private String writer;
    private int totalLikes;
    private int taste;
    private int mood;
    private int park;
    private int kind;
    private int cost;
    private Boolean certified;

    //리뷰 등록
    @Builder
    public ReviewRequestDTO(String writer, int taste, int mood, int park, int kind, int cost, boolean certified) {
        this.writer=writer;
        this.taste = taste;
        this.mood = mood;
        this.park = park;
        this.kind = kind;
        this.cost = cost;
        this.certified = certified;
    }

    //DTO -> Entity
    public Review toSaveReview(){

        return Review.builder()
                .writer(writer)
                .taste(taste)
                .mood(mood)
                .park(park)
                .kind(kind)
                .cost(cost)
                .certified(certified)
                .build();
    }

}
