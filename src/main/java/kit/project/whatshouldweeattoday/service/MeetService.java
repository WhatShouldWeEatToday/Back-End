package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.meet.MeetResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Chat;
import kit.project.whatshouldweeattoday.domain.entity.ChatRoom;
import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.domain.entity.Meet;
import kit.project.whatshouldweeattoday.repository.ChatRepository;
import kit.project.whatshouldweeattoday.repository.ChatRoomRepository;
import kit.project.whatshouldweeattoday.repository.FoodRepository;
import kit.project.whatshouldweeattoday.repository.MeetRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final ChatRoomRepository chatRoomRepository;
    private final MeetRepository meetRepository;
    private final ChatRepository chatRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    @Transactional
    public MeetResponseDTO registerMeetMenu(String maxVotedMenu, Long chatRoomId) throws BadRequestException {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));

        // 중복된 meetMenu가 존재하는지 확인
        Optional<Meet> existingMeet = meetRepository.findByRoomIdAndMeetMenu(chatRoomId, maxVotedMenu);
        if (existingMeet.isPresent()) {
            throw new BadRequestException("해당 메뉴가 이미 채팅방에 등록되어 있습니다.");
        }

        Meet meet = new Meet();
        meet.setId(1L);
        meet.setMeetMenu(maxVotedMenu);

        chatRoom.addMeet(meet);
        chatRoom = chatRoomRepository.saveAndFlush(chatRoom);
        meet = meetRepository.saveAndFlush(meet);

        Chat chat = Chat.createChat(chatRoom, null, meet, SecurityUtil.getLoginId());
        chatRepository.save(chat);

        Food findFood = foodService.findByFoodName(maxVotedMenu).orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        if(findFood == null) {
            throw new BadRequestException("존재하지 않는 음식입니다.");
        }
        findFood.incrementFoodCount();
        foodRepository.save(findFood);

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
        return meetRepository.save(meet);
    }
}
