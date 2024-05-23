package kit.project.whatshouldweeattoday.domain.dto.friend;

import lombok.Getter;

import java.util.List;

@Getter
public class InviteFriendRequestDTO {
    private List<String> friendLoginIds;
}
