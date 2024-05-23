package kit.project.whatshouldweeattoday.domain.dto.chat;

import kit.project.whatshouldweeattoday.domain.type.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomMessage {

    private MessageType messageType;
    private Long userId;
    private String content;
}