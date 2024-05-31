package kit.project.whatshouldweeattoday.domain.dto.chat;

import lombok.Builder;

@Builder
public class ChatResponseDTO {
    private Long id;
//    private String loginId;
    private Long voteId;
    private Long meetId;
}
