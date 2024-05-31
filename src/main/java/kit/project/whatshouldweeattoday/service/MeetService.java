package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
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

    public void registerMeetMenu(String maxVotedMenu, Long chatRoomId) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));
        Meet meet = new Meet();
        meet.setMeetMenu(maxVotedMenu);
        meet.setChatRoom(chatRoom);
        meetRepository.save(meet);
    }
}
