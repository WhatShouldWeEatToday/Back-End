package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPathDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteIdRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final VoteService voteService;
    private final PathService pathService;
    private final MeetService meetService;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final SimpMessageSendingOperations messagingTemplate;


    /**
     * 친구 초대 할 때 초대경로로 메시지 뿌리기
     * @param requestDTO
     */
    @MessageMapping("/invite")
    public void inviteFriendsToRoom(RoomAndFriendsRequestDTO requestDTO) {
        ChatRoom chatRoom = chatRoomService.findByRoomId(requestDTO.getRoomId());
        Set<Member> friends = memberService.findAllByLoginIds(requestDTO.getFriendLoginIds());

        for (Member friend : friends) {
            messagingTemplate.convertAndSend("/topic/public/" + friend.getLoginId(), chatRoom.getId());
        }
    }

    /**
     * 채팅방 내 모든 메시지 조회
     * @param roomId
     */
    @GetMapping("/chat/rooms/{roomId}")
    public ResponseEntity<?> getChatRoomOne(@PathVariable(name = "roomId", required = false) Long roomId) {
        List<ChatResponseDTO> chat = chatService.findChatById(roomId);
        return new ResponseEntity<>(chat, HttpStatus.OK);
    }

    /**
     * 채팅방 내 투표 생성
     * @param roomId
     * @param voteRequest
     */
    @MessageMapping("/vote/register/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public VoteResponseDTO registerVote(@DestinationVariable("roomId") Long roomId, VoteRequestDTO voteRequest) throws BadRequestException {
        try {
            Vote vote = voteService.createVote(voteRequest.getMenu1(), voteRequest.getMenu2());
            chatService.createVote(roomId, voteRequest.getMenu1(), voteRequest.getMenu2());
            return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2());
        } catch (Exception e) {
            log.error("Error registering vote for roomId {}: {}", roomId, e.getMessage());
            throw new BadRequestException("Failed to register vote");
        }
    }

    /**
     * 메뉴 투표 Count 실시간 관리
     * @param voteId
     */
    @MessageMapping("/vote/increment/{roomId}/{voteId}")
    @SendTo("/topic/room/{roomId}")
    public VoteResponseDTO incrementVote(@DestinationVariable("voteId") Long voteId, VoteIdRequestDTO voteRequest) throws BadRequestException {
        try {
            Vote vote = voteService.getVote(voteId);
            vote.setMenu1(voteRequest.getMenu1());
            vote.setMenu2(voteRequest.getMenu2());
            vote.incrementVoteCount1(voteRequest.getVoteCount1());
            vote.incrementVoteCount2(voteRequest.getVoteCount2());

            return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2());
        } catch (Exception e) {
            log.error("Error incrementing vote for voteId {}: {}", voteId, e.getMessage());
            throw new BadRequestException("Failed to increment vote count");
        }
    }

    /**
     * 메뉴 투표 종료 및 메뉴 저장
     * @param voteId
     */
    @MessageMapping("/vote/end/{roomId}/{voteId}")
    @SendTo("/topic/room/{roomId}")
    public VoteResponseDTO endVoteAndSaveMenu(@DestinationVariable Long voteId, Long roomId) throws BadRequestException {
        try {
            Vote vote = voteService.getVote(voteId);

            int memberCount = chatService.getMemberCount(roomId);
            long totalCount = vote.getVoteCount1() + vote.getVoteCount2();
            if (memberCount == totalCount) {
                String maxVotedMenu = voteService.getMostVotedMenu(voteId);
                meetService.registerMeetMenu(maxVotedMenu, roomId);
            }
            return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2());
        } catch (Exception e) {
            log.error("Error incrementing vote for voteId {}: {}", voteId, e.getMessage());
            throw new BadRequestException("Failed to increment vote count");
        }
    }

    /**
     * 채팅방 내 약속 생성
     * @param roomId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/register/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public MeetChatResponseDTO createMeet(@DestinationVariable("roomId") Long roomId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        chatService.createMeet(roomId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetTime());

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
    @SendTo("/topic/room/{roomId}")
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
    @SendTo("/topic/room/{roomId}")
    public void endMeet(@DestinationVariable("meetId") Long meetId) throws BadRequestException {
        chatService.endMeet(meetId);
    }

    /**
     * 출발지 등록
     * @param departures
     */
    @MessageMapping("/departure/register")
    @SendTo("/topic/room/{roomId}")
    public List<PersonalPathDTO> registerDeparture(List<String> departures) {
        return pathService.getWeight("떡볶이", departures);
    }
}
