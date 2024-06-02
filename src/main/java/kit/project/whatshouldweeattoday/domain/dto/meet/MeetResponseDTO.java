package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MeetResponseDTO {
    private Long meetId;
    private String maxVotedMenu;

    public MeetResponseDTO(Long meetId, String maxVotedMenu) {
        this.meetId = meetId;
        this.maxVotedMenu = maxVotedMenu;
    }
}
