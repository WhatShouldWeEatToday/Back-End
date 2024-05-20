package kit.project.whatshouldweeattoday.domain.dto.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class MeetChatResponseDTO {
    private Long roomId;
    private String meetLocate;
    private String meetMenu;
    private Date meetTime;
}
