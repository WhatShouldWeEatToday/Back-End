package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.chat.RoomAndFriendsRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.DepartureResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteIdRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import kit.project.whatshouldweeattoday.repository.VoteRepository;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final VoteService voteService;
    private final MeetService meetService;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final MeetRepository meetRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final VoteRepository voteRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private List<String> departureList = new ArrayList<>();

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
     * 채팅방 내 투표 생성
     * @param roomId
     * @param voteRequest
     */
    @MessageMapping("/vote/register/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public VoteResponseDTO registerVote(@DestinationVariable("roomId") Long roomId, VoteRequestDTO voteRequest) throws BadRequestException {
        try {
            Vote vote = voteService.createVote(voteRequest.getMenu1(), voteRequest.getMenu2());
            chatService.createVote(roomId, vote);

            return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2(), false); //,
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
    public VoteResponseDTO incrementVote(@DestinationVariable("roomId") Long roomId, @DestinationVariable("voteId") Long voteId, VoteIdRequestDTO voteRequest) throws BadRequestException {
        try {
            Vote vote = voteService.getVote(voteId);

            if (vote.getVoteCount1() == null) {
                vote.setVoteCount1(0L);
            }
            if (vote.getVoteCount2() == null) {
                vote.setVoteCount2(0L);
            }

            vote.incrementVoteCount1(voteRequest.getVoteCount1());
            vote.incrementVoteCount2(voteRequest.getVoteCount2());

            voteRepository.save(vote);

            int memberCount = chatService.getMemberCount(roomId);
            long totalCount = vote.getVoteCount1() + vote.getVoteCount2();
            boolean isCountSame = memberCount == totalCount;

            return new VoteResponseDTO(vote.getId(), vote.getMenu1(), vote.getVoteCount1(), vote.getMenu2(), vote.getVoteCount2(), isCountSame);
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
    public MeetResponseDTO endVoteAndSaveMenu(@DestinationVariable("voteId") Long voteId, @DestinationVariable("roomId") Long roomId) throws BadRequestException {
//        try {
            String maxVotedMenu = voteService.getMostVotedMenu(voteId);
            return meetService.registerMeetMenu(maxVotedMenu, roomId);
//        } catch (Exception e) {
//            log.error("Error ending vote for voteId {}: {}", voteId, e.getMessage());
//            throw new BadRequestException("Failed to end vote and save menu", e);
//        }
    }

    /**
     * 채팅방 내 약속 조회
     * @param roomId
     */
    @MessageMapping("/meet/state/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ResponseEntity<?> getMeet(@PathVariable(name = "roomId", required = false) Long roomId) {
        MeetResponseDTO meet = chatService.findMeetById(roomId);
        return new ResponseEntity<>(meet, HttpStatus.OK);
    }

    /**
     * 채팅방 내 약속 생성
     * @param roomId
     * @param meetRequestDTO
     */
    @MessageMapping("/meet/register/{roomId}/{meetId}")
    @SendTo("/topic/room/{roomId}")
    public MeetChatResponseDTO createMeet(@DestinationVariable("roomId") Long roomId, @DestinationVariable("meetId") Long meetId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Meet meet = meetService.findByMeetId(meetId);
        meet.setMeetLocate(meetRequestDTO.getMeetLocate());
        meet.setMeetTime(meetRequestDTO.getMeetTime());

        meetRepository.save(meet);

        return MeetChatResponseDTO.builder()
                .roomId(roomId)
                .meetId(meet.getId())
                .meetMenu(meet.getMeetMenu())
                .meetLocate(meet.getMeetLocate())
                .meetTime(meet.getMeetTime())
                .build();
    }

    /**
     * 채팅방 내 약속 수정
     * @param meetId
     * @param meetRequestDTO
     */
//    @MessageMapping("/meet/update/{roomId}/{meetId}")
//    @SendTo("/topic/room/{roomId}")
//    public MeetChatResponseDTO updateMeet(@DestinationVariable("meetId") Long meetId, MeetRequestDTO meetRequestDTO) throws BadRequestException {
//        Meet updatedMeet = chatService.updateMeet(meetId, meetRequestDTO.getMeetLocate(), meetRequestDTO.getMeetTime());
//
//        return MeetChatResponseDTO.builder()
//                .roomId(updatedMeet.getChat().getRoom().getId())
//                .meetLocate(updatedMeet.getMeetLocate())
//                .meetMenu(updatedMeet.getMeetMenu())
//                .meetTime(updatedMeet.getMeetTime())
//                .build();
//    }

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
    @MessageMapping("/departure/register/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ResponseEntity<DepartureResponseDTO> registerDeparture(@DestinationVariable("roomId") Long roomId, String departures) throws BadRequestException {
        ChatRoom room = chatRoomRepository.findOneById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다.");
        }
        departureList.add(departures);

        String meetMenu = room.getMeet().getMeetMenu();
        String meetDate = room.getMeet().getMeetTime();
        if (meetMenu == null) {
            throw new IllegalArgumentException("해당 채팅방에 대한 Chat 정보가 없습니다.");
        }
        int memberCount = chatService.getMemberCount(roomId);

        DepartureResponseDTO responseDTO = DepartureResponseDTO.builder()
                .memberCount(memberCount)
                .meetMenu(meetMenu)
                .meetDate(meetDate)
                .departureList(departureList)
                .build();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
