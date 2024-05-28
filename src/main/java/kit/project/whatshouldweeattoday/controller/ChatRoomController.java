package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.type.MessageType;
import kit.project.whatshouldweeattoday.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 생성 및 친구 초대
     * @param requestDTO
     */
    @MessageMapping("/chat.createRoomAndInviteFriends")
    @SendTo("/topic/public")
    public ChatRoom createRoomAndInviteFriends(@Payload RoomAndFriendsRequestDTO requestDTO) {
        try {
            return chatRoomService.createRoomAndInviteFriends(requestDTO.getName(), requestDTO.getFriendLoginIds());
        } catch (BadRequestException e) {
            log.error("채팅방 생성 및 친구 초대 오류: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 채팅방 나가기
     * @param roomId
     * @param chatRoomMessage
     */
    @MessageMapping("/chat.endRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatRoomMessage endRoom(@DestinationVariable("roomId") Long roomId, ChatRoomMessage chatRoomMessage) {
        ChatRoom chatRoom = chatRoomService.endRoom(roomId);

        ChatRoomMessage endRoomMessage = new ChatRoomMessage();
        endRoomMessage.setContent("채팅방이 종료되었습니다.");
        endRoomMessage.setMessageType(MessageType.LEAVE);

        return endRoomMessage;
    }
}
