package kit.project.whatshouldweeattoday.domain.dto.chat;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoomMember;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomMemberDTO {
    private Long memberId;
    private String loginId;
    private String nickname;

    public static ChatRoomMemberDTO from(ChatRoomMember chatRoomMember) {
        Member member = chatRoomMember.getMember();
        return new ChatRoomMemberDTO(
                member.getId(),
                member.getLoginId(),
                member.getNickname()
        );
    }
}