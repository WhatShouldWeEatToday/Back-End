package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.service.ChatService;
import kit.project.whatshouldweeattoday.service.PathService;
import kit.project.whatshouldweeattoday.service.VoteService;
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
    private final VoteService voteService;
    private final PathService pathService;

    /**
     * 채팅방 내 투표 생성
     * @param roomId
     * @param voteRequest
     */
    @MessageMapping("/vote/register/{roomId}")
    @SendTo("/topic/votes/{roomId}")
    public VoteResponseDTO registerVote(@DestinationVariable Long roomId, VoteRequestDTO voteRequest) throws BadRequestException {
        Vote vote = voteService.createVote(voteRequest.getMenu1(), voteRequest.getMenu2());
        chatService.createVote(roomId, voteRequest.getMenu1(), voteRequest.getMenu2());

        return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2());
    }

    /**
     * 메뉴 투표 Count 실시간 관리
     * @param voteId
     */
    @MessageMapping("/vote/increment/{voteId}")
    @SendTo("/topic/votes")
    public VoteResponseDTO incrementVote(@DestinationVariable Long voteId) throws BadRequestException {
        voteService.incrementVoteCount1(voteId);
        voteService.incrementVoteCount1(voteId);

        Vote vote = voteService.getVote(voteId);
        return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2());
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
