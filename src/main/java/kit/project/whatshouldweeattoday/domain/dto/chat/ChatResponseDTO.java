package kit.project.whatshouldweeattoday.domain.dto.chat;

import kit.project.whatshouldweeattoday.domain.type.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatResponseDTO (
    Long id,
    Long loginId,
    String nickname,
    MessageType type,
    LocalDateTime createdDate
) {}
