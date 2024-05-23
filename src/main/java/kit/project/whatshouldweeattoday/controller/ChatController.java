package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.VoteChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 채팅 메시지 전송
     * @param chatRoomMessage
     */
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/public")
//    public ChatRoomMessage sendMessage(ChatRoomMessage chatRoomMessage) {
//        return chatRoomMessage;
//    }

    /**
     * 친구 초대
     * @param chatRoomMessage
     * @return
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatRoomMessage addUser(ChatRoomMessage chatRoomMessage) {
        chatRoomMessage.setContent(chatRoomMessage.getUserId() + " joined");
        return chatRoomMessage;
    }

    /**
     * 채팅방 내 투표 생성
     * @param roomId
     * @param voteRequestDTO
     */
    @MessageMapping("/vote/{roomId}") // 여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}") // 구독하고 있는 장소로 메시지 전송(목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public VoteChatResponseDTO createVote(@DestinationVariable("roomId") Long roomId, VoteRequestDTO voteRequestDTO) throws BadRequestException {
        Chat chat = chatService.createVote(roomId, voteRequestDTO.getMenu());

        return VoteChatResponseDTO.builder()
                .roomId(roomId)
                .menu(voteRequestDTO.getMenu())
                .build();
    }

    /**
     * 채팅방 내 투표 종료
     * @param roomId
     */
    @MessageMapping("/vote/end/{roomId}") // 여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}") // 구독하고 있는 장소로 메시지 전송(목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public void endVoteAndStartMeet(@DestinationVariable("roomId") Long roomId) throws BadRequestException {
        chatService.endVote(roomId);
    }

    /**
     * 채팅방 내 약속 생성
     * @param roomId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/{roomId}")
    @SendTo("/room/{roomId}")
    public MeetChatResponseDTO createMeet(@DestinationVariable("roomId") Long roomId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Chat chat = chatService.createMeet(roomId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetMenu(), meetRequestDTO.getMeetTime());

        return MeetChatResponseDTO.builder()
                .roomId(roomId)
                .meetLocate(meetRequestDTO.getMeetLocate())
                .meetMenu(meetRequestDTO.getMeetMenu())
                .meetTime(meetRequestDTO.getMeetTime())
                .build();
    }

    /**
     * 채팅방 내 약속 수정
     * @param meetId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/update/f{roomId}")
    @SendTo("/room/{roomId}")
    public MeetChatResponseDTO updateMeet(@DestinationVariable("meetId") Long meetId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Meet updatedMeet = chatService.updateMeet(meetId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetMenu(), meetRequestDTO.getMeetTime());

        return MeetChatResponseDTO.builder()
                .roomId(updatedMeet.getChat().getRoom().getId())
                .meetLocate(updatedMeet.getMeetLocate())
                .meetMenu(updatedMeet.getMeetMenu())
                .meetTime(updatedMeet.getMeetTime())
                .build();
    }

    /**
     * 채팅방 내 약속 종료
     * @param meetId
     */
    @MessageMapping("/meet/end/{meetId}")
    @SendTo("/room/{roomId}")
    public void endMeet(@DestinationVariable("meetId") Long meetId) throws BadRequestException {
        chatService.endMeet(meetId);
    }
}
