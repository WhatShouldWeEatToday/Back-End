package kit.project.whatshouldweeattoday.domain.dto.likes;

import kit.project.whatshouldweeattoday.domain.entity.Likes;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikesResponseDTO {
    private Long id;
    private Boolean state;

    @Builder
    public LikesResponseDTO(Long id, Boolean state) {
        this.id = id;
        this.state = state;
    }

    public LikesResponseDTO(Likes likes){
        this.id = likes.getId();
        this.state = likes.getState();
    }
}
