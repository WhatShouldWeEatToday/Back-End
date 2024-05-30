package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.VoteChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.service.ChatService;
import kit.project.whatshouldweeattoday.service.NoticeService;
import kit.project.whatshouldweeattoday.service.PathService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final PathService pathService;
    private final NoticeService noticeService;

    /**
     * 채팅방 내 투표 생성
     * @param roomId
     * @param voteRequestDTO
     */
    @MessageMapping("/vote/register/{roomId}") // 여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/topic/{roomId}") // 구독하고 있는 장소로 메시지 전송(목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public VoteChatResponseDTO createVote(@DestinationVariable("roomId") Long roomId, VoteRequestDTO voteRequestDTO) throws BadRequestException {
        Chat chat = chatService.createVote(roomId, voteRequestDTO.getMenu());

//        noticeService.sendNotice("새로운 투표가 생성되었습니다.", NoticeType.VOTE, SecurityUtil.getLoginId()); // userId를 실제 사용자 id로 변경

        return VoteChatResponseDTO.builder()
                .roomId(roomId)
                .menu(voteRequestDTO.getMenu())
                .build();
    }

    /**
     * 채팅방 내 투표 종료
     * @param roomId
     */
    @MessageMapping("/vote/end/{roomId}")
    @SendTo("/topic/{roomId}")
    public void endVoteAndCreateMeet(@DestinationVariable("roomId") Long roomId) throws BadRequestException {
        chatService.endVoteAndCreateMeet(roomId);
    }

    /**
     * 채팅방 내 약속 생성
     * @param roomId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/register/{roomId}")
    @SendTo("/topic/{roomId}")
    public MeetChatResponseDTO createMeet(@DestinationVariable("roomId") Long roomId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Chat chat = chatService.createMeet(roomId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetTime());

        return MeetChatResponseDTO.builder()
                .roomId(roomId)
                .meetLocate(meetRequestDTO.getMeetLocate())
                .meetTime(meetRequestDTO.getMeetTime())
                .build();
    }

    /**
     * 채팅방 내 약속 수정
     * @param meetId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/update/{roomId}")
    @SendTo("/topic/{roomId}")
    public MeetChatResponseDTO updateMeet(@DestinationVariable("meetId") Long meetId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Meet updatedMeet = chatService.updateMeet(meetId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetTime());

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
    @SendTo("/topic/{roomId}")
    public void endMeet(@DestinationVariable("meetId") Long meetId) throws BadRequestException {
        chatService.endMeet(meetId);
    }

    /**
     * 출발지 등록
     * @param departures
     */
    @MessageMapping("/departure/register")
    @SendTo("/topic/public")
    public List<PersonalPath> registerDeparture(List<String> departures) {
        return pathService.getWeight("떡볶이", departures);
    }
}
