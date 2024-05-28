package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.dto.friend.InviteFriendRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /**
     * 채팅방 친구 초대
     * @param roomId
     * @param inviteRequestDTO
     */
    @MessageMapping("/chat.inviteFriend/{roomId}")
    @SendTo("/topic/{roomId}")
    public void inviteFriends(@DestinationVariable("roomId") Long roomId,
                              @Payload InviteFriendRequestDTO inviteRequestDTO) {
        try {
            chatRoomService.inviteFriends(roomId, inviteRequestDTO.getFriendLoginIds());
        } catch (BadRequestException e) {
            log.error("친구 초대 오류: {}", e.getMessage());
        }
    }

    /**
     * 채팅방 등록
     * @param chatRoom
     */
//    @PostMapping("/room")
//    public String createRoom(ChatRoomDTO chatRoom) {
//        chatRoomService.createChatRoom(chatRoom.getName());
//        return "redirect:/roomList";
//    }

//    @MessageMapping("/chat.createRoom")
//    @SendTo("/topic/public")
//    public ChatRoomMessage createRoom(ChatRoomMessage chatRoomMessage) {
//        // 채팅방 생성 로직 추가
//        return chatRoomMessage;
//    }

    /**
     * 채팅방 참여
     * @param roomId
     */
    @GetMapping("/{roomId}")
    public String joinRoom(@PathVariable("roomId") Long roomId, Model model) {
        List<Chat> chatList = chatRoomService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "chat/room";
    }

    /**
     * 채팅방 리스트 보기
     */
    @GetMapping("/roomList")
    public String roomList(Model model) {
        List<ChatRoom> roomList = chatRoomService.findAllChatRoom();
        model.addAttribute("roomList", roomList);
        return "chat/roomList";
    }

    /**
     * 채팅방 나가기
     * @param roomId
     * @param chatRoomMessage
     */
    @MessageMapping("/chat.endRoom/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatRoomMessage endRoom(@DestinationVariable("roomId") Long roomId, ChatRoomMessage chatRoomMessage) {
        // 채팅방 종료 로직 추가
        return chatRoomMessage;
    }
}
