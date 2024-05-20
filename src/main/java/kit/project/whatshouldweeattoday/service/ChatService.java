package kit.project.whatshouldweeattoday.service;

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
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final VoteRepository voteRepository;
    private final MeetRepository meetRepository;

    /**
     * 채팅방 생성
     * @param name 방 이름
     */
    public ChatRoom createChatRoom(String name) {
        return chatRoomRepository.save(ChatRoom.createRoom(name));
    }

    /**
     * 모든 채팅방 찾기
     */
    public List<ChatRoom> findAllChatRoom() {
        return chatRoomRepository.findAll();
    }


    /**
     * 채팅방의 채팅 내용 불러오기
     * @param roomId 채팅방 id
     */
    public List<Chat> findAllChatByRoomId(Long roomId) {
        return chatRepository.findAllByRoomId(roomId);
    }

    /**
     * 채팅에서 투표 생성
     * @param roomId 채팅방 id
     * @param menu 메뉴 이름
     */
    public Chat createVote(Long roomId, String menu) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Vote vote = voteRepository.save(Vote.createVote(menu));
        return chatRepository.save(Chat.createChat(chatRoom, vote, null, SecurityUtil.getLoginId()));
    }

    /**
     * 투표 종료
     * @param roomId 채팅방 id
     */
    public void endVote(Long roomId) throws BadRequestException {
        Chat chat = chatRepository.findTopByRoomIdAndVoteNotNullOrderBySendDateDesc(roomId).orElseThrow(() -> new BadRequestException("진행 중인 투표가 없습니다."));
        Vote vote = chat.getVote();
        // 투표 종료 로직을 여기에 추가합니다 (예: 상태 변경)
    }

    /**
     * 채팅에서 약속 생성
     * @param roomId 채팅방 id
     * @param meetLocate 약속 장소
     * @param meetMenu 약속 메뉴
     * @param meetTime 약속 시간
     */
    public Chat createMeet(Long roomId, String meetLocate, String meetMenu, Date meetTime) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Meet meet = meetRepository.save(Meet.createMeet(meetLocate, meetMenu, meetTime));
        return chatRepository.save(Chat.createChat(chatRoom, null, meet, SecurityUtil.getLoginId()));
    }

    /**
     * 약속 수정
     * @param meetId 약속 id
     * @param meetLocate 약속 장소
     * @param meetMenu 약속 메뉴
     * @param meetTime 약속 시간
     */
    public Meet updateMeet(Long meetId, String meetLocate, String meetMenu, Date meetTime) throws BadRequestException {
        Meet meet = meetRepository.findById(meetId).orElseThrow(() -> new BadRequestException("존재하지 않는 약속입니다."));
        meet.updateMeet(meetLocate, meetMenu, meetTime);
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
