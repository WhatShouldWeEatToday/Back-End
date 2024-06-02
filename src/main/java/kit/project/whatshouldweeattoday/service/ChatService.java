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
            throw new IllegalArgumentException("해당 채팅방에 대한 Chat 정보가 없습니다.");
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
            throw new IllegalArgumentException("해당 채팅방에 대한 Chat 정보가 없습니다.");
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
        voteRepository.save(vote);
        vote = voteRepository.findById(vote.getId()).orElseThrow(() -> new BadRequestException("Vote not found")); // <-- 추가된 부분
        chatRoomRepository.save(chatRoom);
        chatRepository.save(Chat.createChat(chatRoom, vote, null, SecurityUtil.getLoginId()));
    }


    public int getMemberCount(Long chatRoomId) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        return chatRoom.getCurrentUserNum();
    }

    /**
     * 투표 종료
     * @param roomId 채팅방 id
     */
    public void endVoteAndCreateMeet(Long roomId) throws BadRequestException {
//        Chat chat = chatRepository.findTopByRoomIdAndVoteNotNullOrderBySendDateDesc(roomId)
//                .orElseThrow(() -> new BadRequestException("진행 중인 투표가 없습니다."));
//        Vote vote = chat.getVote();
//        String selectedMenu = vote.getSelectedMenu();
//        if (selectedMenu == null || selectedMenu.isEmpty()) {
//            throw new BadRequestException("선택된 메뉴가 없습니다.");
//        }
//
//        // 기존 약속 종료 로직 추가
//        Meet existingMeet = chat.getMeet();
//        if (existingMeet != null) {
//            endMeet(existingMeet.getId());
//        }
//
//        // 새로운 약속 생성
//        LocalDateTime meetTime = LocalDateTime.now().plusDays(1);
//        String meetLocate = "기본 장소";
//        Chat createdChat = createMeet(roomId, meetLocate, meetTime, selectedMenu);
//
//        // MeetMenu와 ChatId를 함께 전달하여 출발지 등록
//        pathService.registerDeparture(selectedMenu, createdChat.getId());
    }

    /**
     *
     * @param roomId
     * @param meetLocate
     * @param meetTime
     */
    public Chat createMeet(Long roomId, String meetLocate, LocalDateTime meetTime) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Meet meet = Meet.createMeet(meetLocate, meetTime);
        meet = meetRepository.save(meet);

        foodService.increaseFoodCount(meetLocate);

        return chatRepository.save(Chat.createChat(chatRoom, null, meet, SecurityUtil.getLoginId()));
    }

    /**
     *
     * @param meetId
     * @param meetLocate
     * @param meetTime
     */
    public Meet updateMeet(Long meetId, String meetLocate, LocalDateTime meetTime) throws BadRequestException {
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
