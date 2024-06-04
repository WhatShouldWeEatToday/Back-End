package kit.project.whatshouldweeattoday.domain.dto.friend;

import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FriendStatusResponseDTO {
    private FriendshipStatus status;

    @Builder
    public FriendStatusResponseDTO(FriendshipStatus status) {
        this.status = status;
    }
}
