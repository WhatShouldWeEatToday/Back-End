package kit.project.whatshouldweeattoday.domain.entity;

import kit.project.whatshouldweeattoday.domain.dto.chat.MeetChatResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.meet.MeetResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPathDTO;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import kit.project.whatshouldweeattoday.service.ChatService;
import kit.project.whatshouldweeattoday.service.MeetService;
import kit.project.whatshouldweeattoday.service.PathService;
import kit.project.whatshouldweeattoday.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Slf4j
@org.springframework.web.bind.annotation.RestController
@RequiredArgsConstructor
public class VoteAndMeetController {

    private final ChatService chatService;
    private final VoteService voteService;
    private final MeetService meetService;
    private final PathService pathService;
    private final MeetRepository meetRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostMapping("/vote/end/{roomId}/{voteId}")
    public MeetResponseDTO endVoteAndSaveMenu(@PathVariable("voteId") Long voteId, @PathVariable("roomId") Long roomId) throws BadRequestException {
        try {
            Vote vote = voteService.getVote(voteId);

            int memberCount = chatService.getMemberCount(roomId);

            long totalCount = vote.getVoteCount1() + vote.getVoteCount2();
            String maxVotedMenu = "";
            MeetResponseDTO responseDTO = null;
            if (memberCount == totalCount) {
                maxVotedMenu = voteService.getMostVotedMenu(voteId);
                responseDTO = meetService.registerMeetMenu(maxVotedMenu, roomId);
            }
            return responseDTO;
        } catch (Exception e) {
            log.error("Error ending vote for voteId {}: {}", voteId, e.getMessage());
            throw new BadRequestException("Failed to end vote and save menu", e);
        }
    }

    @PostMapping("/meet/register/{roomId}/{meetId}")
    public MeetChatResponseDTO createMeet(@PathVariable("roomId") Long roomId, @PathVariable("meetId") Long meetId, @RequestBody MeetRequestDTO meetRequestDTO) throws BadRequestException {
        Meet meet = meetService.findByMeetId(meetId);
        meet.setMeetLocate(meetRequestDTO.getMeetLocate());
        meet.setMeetTime(meetRequestDTO.getMeetTime());

        meetRepository.save(meet);

        return MeetChatResponseDTO.builder()
                .roomId(roomId)
                .meetLocate(meet.getMeetLocate())
                .meetTime(meet.getMeetTime())
                .build();
    }

    @PostMapping("/departure/register/{roomId}")
    public ResponseEntity<List<PersonalPathDTO>> registerDeparture(@PathVariable("roomId") Long roomId, @RequestBody List<String> departures) {
        ChatRoom room = chatRoomRepository.findOneById(roomId);
        if (room == null) {
            throw new IllegalArgumentException("존재하지 않는 채팅방입니다.");
        }

        String meetMenu = room.getMeet().getMeetMenu();
        if (meetMenu == null) {
            throw new IllegalArgumentException("해당 채팅방에 대한 Chat 정보가 없습니다.");
        }
        List<PersonalPathDTO> weight = pathService.getWeight(meetMenu, departures);
        return new ResponseEntity<>(weight, HttpStatus.OK);
    }
}
