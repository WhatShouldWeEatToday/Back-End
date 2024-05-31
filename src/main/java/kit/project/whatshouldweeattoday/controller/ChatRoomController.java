package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.type.MessageType;
import kit.project.whatshouldweeattoday.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 채팅방 생성 및 친구 초대
     * @param requestDTO
     */
    @MessageMapping("/chat.createRoomAndInviteFriends") // {1}
    public void createRoomAndInviteFriends(@Payload RoomAndFriendsRequestDTO requestDTO) {  // {2}
        ChatRoom chatRoom;
        try {
            chatRoom = chatRoomService.createRoomAndInviteFriends(requestDTO.getName(), requestDTO.getFriendLoginIds());  // {3}
        } catch (BadRequestException e) {
            log.error("채팅방 생성 및 친구 초대 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            log.error("채팅방 생성 및 친구 초대 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", e);
        }

        for (String friendLoginId : requestDTO.getFriendLoginIds()) {
            String topic = "/topic/public/" + friendLoginId;  // {4}
            messagingTemplate.convertAndSend(topic, chatRoom.getId());  // {5}
        }
    }

    /**
     * 채팅방 참여
     * @param chatRoomMessage
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatRoomMessage addUser(ChatRoomMessage chatRoomMessage) {
        chatRoomMessage.setContent(chatRoomMessage.getLoginId() + " joined");
        return chatRoomMessage;
    }

    /**
     * 채팅방 나가기
     * @param roomId
     */
    @MessageMapping("/chat.endRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatRoomMessage endRoom(@DestinationVariable("roomId") Long roomId) {
        ChatRoom chatRoom = chatRoomService.endRoom(roomId);

        ChatRoomMessage endRoomMessage = new ChatRoomMessage();
        endRoomMessage.setContent("채팅방이 종료되었습니다.");
        endRoomMessage.setMessageType(MessageType.LEAVE);

        return endRoomMessage;
    }
}