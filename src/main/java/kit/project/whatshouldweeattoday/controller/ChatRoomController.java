package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    /**
     * 채팅방 등록
     * @param chatRoom
\     */
    @PostMapping("/room")
    public String createRoom(ChatRoomDTO chatRoom) {
        chatService.createChatRoom(chatRoom.getName());
        return "redirect:/roomList";
    }

    /**
     * 채팅방 참여하기
     * @param roomId 채팅방 id
     */
    @GetMapping("/{roomId}")
    public String joinRoom(@PathVariable("roomId") Long roomId, Model model) {
        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("chatList", chatList);
        return "chat/room";
    }

    /**
     * 채팅방 리스트 보기
     */
    @GetMapping("/roomList")
    public String roomList(Model model) {
        List<ChatRoom> chatRoomList = chatService.findAllChatRoom();
        model.addAttribute("chatRoomList", chatRoomList);
        return "chat/roomList";
    }

    /**
     * html 띄우기
     */
    @GetMapping("/roomForm")
    public String roomForm() {
        return "chat/roomForm";
    }
//    /**
//     * 채팅방 참여하기
//     * @param roomId 채팅방 id
//     */
//    @GetMapping("/{roomId}")
//    public String joinRoom(@PathVariable("roomId") Long roomId, Model model) {
//        List<Chat> chatList = chatService.findAllChatByRoomId(roomId);
//
//        model.addAttribute("roomId", roomId);
//        model.addAttribute("chatList", chatList);
//        return "chat/room";
//    }
//
//    /**
//     * 채팅방 리스트 보기
//     */
//    @GetMapping("/roomList")
//    public String roomList(Model model) {
//        List<ChatRoom> chatRoomList = chatService.findAllChatRoom();
//        model.addAttribute("chatRoomList", chatRoomList);
//        return "chat/roomList";
//    }
//
//    /**
//     * html 띄우기
//     */
//    @GetMapping("/roomForm")
//    public String roomForm() {
//        return "chat/roomForm";
//    }
}
