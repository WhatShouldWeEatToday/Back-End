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
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
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
    @MessageMapping("/chat.createRoomAndInviteFriends") // PUB
    @SendTo("/topic/public/{friendLoginId}") // SUB
    public Long createRoomAndInviteFriends(@Payload RoomAndFriendsRequestDTO requestDTO, SimpMessageHeaderAccessor headerAccessor) {
        ChatRoom roomAndInviteFriends;
        try {
            // 채팅방 생성 및 친구 초대 로직
            roomAndInviteFriends = chatRoomService.createRoomAndInviteFriends(requestDTO.getName(), requestDTO.getFriendLoginIds());
        } catch (BadRequestException e) {
            // BadRequestException이 발생한 경우 클라이언트에게 오류 메시지를 보냅니다.
            log.error("채팅방 생성 및 친구 초대 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            // 기타 예외가 발생한 경우 서버 오류 메시지를 보냅니다.
            log.error("채팅방 생성 및 친구 초대 오류: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", e);
        }
        // 클라이언트로부터 전달된 friendLoginId를 추출합니다.
        String friendLoginId = (String) headerAccessor.getSessionAttributes().get("friendLoginId");

        // 추출한 friendLoginId를 사용하여 실제 주제 경로를 생성합니다.
        String topic = "/topic/public/" + friendLoginId;

        // 생성된 주제 경로로 메시지를 전송합니다.
        messagingTemplate.convertAndSend(topic, roomAndInviteFriends.getId());

        return roomAndInviteFriends.getId();
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
