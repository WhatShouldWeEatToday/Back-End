package kit.project.whatshouldweeattoday.domain.dto.review;

import kit.project.whatshouldweeattoday.domain.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDTO {

    private Long id;
    private String writer;
    private Long totalLikes;
    private int taste;
    private int cost;
    private int kind;
    private int mood;
    private int park;
    private Boolean certified;




    // review return 할때
    public ReviewResponseDTO(Review review) {
       this.id = review.getId();
       this.writer = review.getWriter();
       this.cost = review.getCost();
       this.park = review.getPark();
       this.mood = review.getMood();
       this.kind = review.getKind();
       this.taste = review.getTaste();
       this.certified = review.getCertified();
       this.totalLikes = review.getTotalLikes();
    }

}
