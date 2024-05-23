package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;

    /**
     * 채팅방에 친구 초대
     * @param roomId 채팅방 id
     * @param friendIds 친구 id 리스트
     */
    public void inviteFriends(Long roomId, List<String> friendIds) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Set<Member> friends = memberRepository.findAllByLoginIdIn(friendIds);
        if (friends.isEmpty()) {
            throw new BadRequestException("초대할 친구가 없습니다.");
        }
        chatRoom.addMembers(friends);
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 생성
     * @param name 방 이름
     */
    public ChatRoom createChatRoom(String name) {
        return chatRoomRepository.save(ChatRoom.createRoom(name));
    }

//    public ChatRoom createChatRoom(String name) {
//        return chatRoomRepository.save(ChatRoom.createRoom(name));
//    }

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
