package kit.project.whatshouldweeattoday.domain.dto.chat;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomDTO {
    private Long roomId;
    private String name;
}
