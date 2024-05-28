package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.chat.ChatRoomMessage;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    /**
     * 채팅방 생성 및 친구 초대
     * @param name
     * @param friendIds
     */

    public ChatRoom createRoomAndInviteFriends(String name, List<String> friendIds) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.createRoom(name));

        // 친구를 채팅방에 초대
        Set<Member> friends = memberRepository.findAllByLoginIdIn(friendIds);
        if (friends.isEmpty()) {
            throw new BadRequestException("초대할 친구가 없습니다.");
        }
        chatRoom.addMembers(friends);
        chatRoomRepository.save(chatRoom);

//        for (Member friend : friends) {
//            Notice notice = new Notice();
//            notice.setContent(friend.getNickname() + "님을 " + chatRoom.getName() + "채팅방에 초대합니다!");
//            notice.setNoticeType(NoticeType.CHAT_INVITE);
//            notice.setUserId(friend.getId());
//            noticeRepository.save(notice);
//        }

        return chatRoom;
    }

    public ChatRoom endRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        chatRoom = chatRoomRepository.save(chatRoom);

        return chatRoom;
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
}
