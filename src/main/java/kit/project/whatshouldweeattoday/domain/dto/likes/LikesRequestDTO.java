package kit.project.whatshouldweeattoday.domain.dto.likes;

import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;

@NoArgsConstructor
@Getter
@Setter
public class LikesRequestDTO {
    private Boolean state;
    private Review review;

    @Builder
    public LikesRequestDTO(Review review,Boolean state){
        this.review=review;
        this.state=state;
    }
    public Likes toSaveEntity(Member member, Review review){
        System.out.println("좋아요 테이블에 리뷰 저장");
        return Likes.builder()
                .member(member)
                .review(review)
                .state(state)
                .build();
    }
}
