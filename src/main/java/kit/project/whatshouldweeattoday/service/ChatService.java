package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.meet.MeetResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.vote.VoteResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.domain.entity.Vote;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import kit.project.whatshouldweeattoday.repository.VoteRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final MeetRepository meetRepository;
    private final VoteRepository voteRepository;
    private final FoodService foodService;

    /**
     * 채팅방 내 투표 조회
     * @param roomId
     */
    @Transactional
    public VoteResponseDTO findVoteById(Long roomId) {
        log.info("채팅룸 메시지 조회를 시작합니다. [roomId : {}]", roomId);
        Chat chat = chatRepository.findOneByRoomId(roomId);
        Vote vote = chat.getVote();
        if (vote == null) {
            throw new IllegalArgumentException("해당 채팅방에 대한 Vote 정보가 없습니다.");
        }
        return VoteResponseDTO.builder()
                .voteId(vote.getId())
                .menu1(vote.getMenu1())
                .menu2(vote.getMenu2())
                .voteCount1(vote.getVoteCount1())
                .voteCount2(vote.getVoteCount2())
                .build();
    }

    /**
     * 채팅방 내 약속 조회
     * @param roomId
     */
    @Transactional
    public MeetResponseDTO findMeetById(Long roomId) {
        log.info("채팅룸 메시지 조회를 시작합니다. [roomId : {}]", roomId);
        Chat chat = chatRepository.findOneByRoomId(roomId);
        Meet meet = chat.getMeet();
        if (meet == null) {
            throw new IllegalArgumentException("해당 채팅방에 대한 Meet 정보가 없습니다.");
        }
        return MeetResponseDTO.builder()
                .meetId(meet.getId())
                .maxVotedMenu(meet.getMeetMenu())
                .build();
    }

    /**
     * @param roomId
     * @param vote
     */
    public void createVote(Long roomId, Vote vote) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        chatRoom.addVote(vote);
//        vote.setVoteCount1(0L); // <-- 추가된 부분
//        vote.setVoteCount2(2L); // <-- 추가된 부분
        voteRepository.save(vote);
        vote = voteRepository.findById(vote.getId()).orElseThrow(() -> new BadRequestException("Vote not found"));
        chatRoomRepository.save(chatRoom);
        chatRepository.save(Chat.createChat(chatRoom, vote, null, SecurityUtil.getLoginId()));
    }


    public int getMemberCount(Long chatRoomId) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        return chatRoom.getCurrentUserNum();
    }

    /**
     *
     * @param meetId
     * @param meetLocate
     * @param meetTime
     */
    public Meet updateMeet(Long meetId, String meetLocate, String meetTime) throws BadRequestException {
        Meet meet = meetRepository.findById(meetId).orElseThrow(() -> new BadRequestException("존재하지 않는 약속입니다."));
        meet.updateMeet(meetLocate, meetTime);

        foodService.increaseFoodCount(meetLocate);

        return meetRepository.save(meet);
    }

    /**
     * 약속 종료
     * @param meetId 약속 id
     */
    public void endMeet(Long meetId) throws BadRequestException {
        Meet meet = meetRepository.findById(meetId).orElseThrow(() -> new BadRequestException("존재하지 않는 약속입니다."));
        // 약속 종료 로직을 여기에 추가합니다 (예: 상태 변경)
        meetRepository.save(meet);
    }
}
