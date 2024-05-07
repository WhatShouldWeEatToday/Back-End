package kit.project.whatshouldweeattoday.domain.dto.review;

import kit.project.whatshouldweeattoday.domain.entity.Member;
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
    private Long member_id;


    // review return 할때
    public ReviewResponseDTO(Review review) {
        if (review.getMember() != null) {
            this.member_id = review.getMember().getId();
        } else {
            this.member_id = null;// 기존 크롤링한 데이터에는 member_id가 없음
        }
        if(review.getReviewType()!=null){
            this.reviewType=review.getReviewType();
        }else{
            this.reviewType=ReviewType.NOT_CERTIFY;
        }
       this.id = review.getId();
       this.writers = review.getWriter();
       this.cost = review.getCost();
       this.park = review.getPark();
       this.mood = review.getMood();
       this.kind = review.getKind();
       this.taste = review.getTaste();
       this.totalLikes = review.getTotalLikes();
       this.created_Date = review.getCreatedDate();
       this.stars=review.getStars();
    }
}
