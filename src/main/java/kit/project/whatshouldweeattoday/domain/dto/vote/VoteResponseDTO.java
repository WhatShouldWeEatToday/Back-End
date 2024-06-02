package kit.project.whatshouldweeattoday.domain.dto.vote;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoteResponseDTO {
    private Long voteId;
    private String menu1;
    private Long voteCount1;
    private String menu2;
    private Long voteCount2;

    public VoteResponseDTO(Long voteId, String menu1, Long voteCount1, String menu2, Long voteCount2) {
        this.voteId = voteId;
        this.menu1 = menu1;
        this.voteCount1 = voteCount1;
        this.menu2 = menu2;
        this.voteCount2 = voteCount2;
    }
}
