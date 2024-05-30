package kit.project.whatshouldweeattoday.domain.dto.chat;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VoteChatResponseDTO {

    private Long roomId;
    private Long menu1Count;
    private Long menu2Count;
}
