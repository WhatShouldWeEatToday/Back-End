package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.meet.MeetResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final ChatRoomRepository chatRoomRepository;
    private final MeetRepository meetRepository;
    private final ChatRepository chatRepository;

    public MeetResponseDTO registerMeetMenu(String maxVotedMenu, Long chatRoomId) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Meet meet = new Meet();
        meet.setMeetMenu(maxVotedMenu);
        meet.setChatRoom(chatRoom);
        meetRepository.save(meet);

        Chat chat = Chat.createChat(chatRoom, null, meet, SecurityUtil.getLoginId());
        chatRepository.save(chat);

        return MeetResponseDTO
                .builder()
                .meetId(meet.getId())
                .maxVotedMenu(maxVotedMenu)
                .build();
    }

    public Meet findByMeetId(Long meetId) throws BadRequestException {
        return meetRepository.findById(meetId).orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
    }

    public Meet save(Meet meet) {
        return meetRepository.save(meet);  // 변경된 객체를 저장
    }
}
