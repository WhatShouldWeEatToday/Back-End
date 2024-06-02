package kit.project.whatshouldweeattoday.domain.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ChatResponseDTO {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("voteId")
    private Long voteId;
    @JsonProperty("meetId")
    private Long meetId;
}
