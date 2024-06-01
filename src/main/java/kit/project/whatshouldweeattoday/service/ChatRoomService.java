package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
//        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId())
//                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다."));

        ChatRoom chatRoom = ChatRoom.createRoom(name);
//        chatRoom.addMember(member);

        Set<Member> friends = memberRepository.findAllByLoginIdIn(friendIds);
        if (friends.isEmpty()) {
            throw new BadRequestException("초대할 친구가 없습니다.");
        }

        chatRoom.addMembers(friends);
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        log.info("채팅방 생성 아이디={}", savedChatRoom.getId());

        return savedChatRoom;
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
    public List<ChatRoom> findChatRoomsByMemberId(Long memberId) {
        return chatRoomRepository.findChatRoomsByMemberId(memberId);
    }

    /**
     * 채팅방의 채팅 내용 불러오기
     * @param roomId 채팅방 id
     */
    public List<Chat> findAllChatByRoomId(Long roomId) {
        return chatRepository.findAllByRoomId(roomId);
    }
}
