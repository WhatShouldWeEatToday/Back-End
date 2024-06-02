package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ChatRoom createRoomAndInviteFriends(String roomName, Member creator, Set<Member> friends) {
        // 영속성 컨텍스트 내로 병합
        creator = memberRepository.findById(creator.getId()).orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        friends = friends.stream()
                .map(friend -> memberRepository.findById(friend.getId()).orElseThrow(() -> new IllegalArgumentException("Friend not found")))
                .collect(Collectors.toSet());

        ChatRoom chatRoom = ChatRoom.createRoom(creator.getLoginId(), roomName);
        chatRoom.addParticipant(creator);

        for (Member friend : friends) {
            chatRoom.addParticipant(friend);
        }

        return chatRoomRepository.save(chatRoom);
    }

    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAll();
    }

    public ChatRoom findByRoomId(Long roomId) {
        return chatRoomRepository.findById(roomId) .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
    }
}
