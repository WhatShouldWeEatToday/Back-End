package kit.project.whatshouldweeattoday.domain.dto.review;

import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.domain.type.ReviewType;
import lombok.Getter;

@Getter
public class ReviewResponseDTO {

    private Long id;
    private Long totalLikes;
    private String writers;
    private int taste;
    private int cost;
    private int kind;
    private int mood;
    private int park;
    private String created_Date;
    private ReviewType reviewType;
    private double stars;


    // review return 할때
    public ReviewResponseDTO(Review review) {
       this.id = review.getId();
       this.writers = review.getWriter();
       this.cost = review.getCost();
       this.park = review.getPark();
       this.mood = review.getMood();
       this.kind = review.getKind();
       this.taste = review.getTaste();
       this.reviewType = review.getReviewType();
       this.totalLikes = review.getTotalLikes();
       this.created_Date = review.getCreatedDate();
       this.stars=review.getStars();
    }
}
