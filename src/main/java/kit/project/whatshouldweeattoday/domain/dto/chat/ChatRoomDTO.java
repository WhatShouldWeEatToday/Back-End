package kit.project.whatshouldweeattoday.domain.dto.chat;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoomMember;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String hostId;
    private String roomName;
    private int currentUserNum;
    private Set<ChatRoomMemberDTO> participants;

    public static ChatRoomDTO from(ChatRoom chatRoom, String loginId) {
        Set<ChatRoomMemberDTO> participants = chatRoom.getChatRoomMembers().stream()
                .filter(member -> !member.getMember().getLoginId().equals(loginId))
                .map(ChatRoomMemberDTO::from)
                .collect(Collectors.toSet());

        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getHostId(),
                chatRoom.getRoomName(),
                chatRoom.getCurrentUserNum(),
                participants
        );
    }
}
