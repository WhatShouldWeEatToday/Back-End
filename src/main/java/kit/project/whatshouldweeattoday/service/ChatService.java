package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.*;
import kit.project.whatshouldweeattoday.repository.*;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final VoteRepository voteRepository;
    private final MeetRepository meetRepository;
    private final MemberRepository memberRepository;
    private final FoodService foodService;
    private final NoticeService noticeService;

    /**
     * 채팅에서 투표 생성
     * @param roomId 채팅방 id
     * @param menu 메뉴 이름
     */
    public Chat createVote(Long roomId, String menu) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Vote vote = voteRepository.save(Vote.createVote(menu));

        List<Member> members = memberRepository.findAllByChatRoomId(roomId);
//        for (Member member : members) {
//            noticeService.sendNotice("메뉴가 선정되었습니다. 오늘의 메뉴는? " + menu, NoticeType.VOTE, member.getLoginId());
//        }

        return chatRepository.save(Chat.createChat(chatRoom, vote, null, SecurityUtil.getLoginId()));
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
