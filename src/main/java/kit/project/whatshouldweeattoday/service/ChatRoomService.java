package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Notice;
import kit.project.whatshouldweeattoday.domain.type.NoticeType;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final NoticeRepository noticeRepository;

    @Transactional
    public synchronized ChatRoom createRoomAndInviteFriends(String roomName, Member creator, Set<Member> friends) {
        log.info("Creating room: {} by creator: {}", roomName, creator.getLoginId());

        creator = memberRepository.findById(creator.getId()).orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        friends = friends.stream()
                .map(friend -> memberRepository.findById(friend.getId()).orElseThrow(() -> new IllegalArgumentException("Friend not found")))
                .collect(Collectors.toSet());

        try {
            ChatRoom chatRoom = ChatRoom.createRoom(creator.getLoginId(), roomName);
            chatRoom.addParticipant(creator);

            for (Member friend : friends) {
                chatRoom.addParticipant(friend);
                String content = creator.getNickname() + "님이 채팅방에 초대하셨습니다.";
                Notice notice = new Notice(friend, content, NoticeType.CHAT_INVITE);
                noticeRepository.save(notice);
            }

            return chatRoomRepository.save(chatRoom);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Room already exists", e);
        }

    }

    public List<ChatRoom> findAllRoom() {
        return chatRoomRepository.findAllWithMembers();
    }

    public ChatRoom findByRoomId(Long roomId) {
        return chatRoomRepository.findById(roomId) .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
    }
}
