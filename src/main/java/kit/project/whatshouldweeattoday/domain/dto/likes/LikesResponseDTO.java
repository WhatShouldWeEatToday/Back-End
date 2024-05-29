package kit.project.whatshouldweeattoday.domain.dto.likes;

import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikesResponseDTO {
    private Long id;
    private Boolean state;
    private Review review;
    private String writer;
    @Builder
    public LikesResponseDTO(Long id, Boolean state) {
        this.id = id;
        this.state = state;
    }

    public LikesResponseDTO(Likes likes){
        this.id = likes.getId();
        this.state = likes.getState();
        this.review=likes.getReview();
        this.writer = likes.getReview().getWriter();
    }
}
