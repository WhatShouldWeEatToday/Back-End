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
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MeetService {

    private final ChatRoomRepository chatRoomRepository;
    private final MeetRepository meetRepository;
    private final ChatRepository chatRepository;
    private final FoodRepository foodRepository;
    private final FoodService foodService;

    @Transactional
    public MeetResponseDTO registerMeetMenu(String maxVotedMenu, Long chatRoomId) throws BadRequestException {
        // 채팅방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 채팅방입니다."));

        // 중복된 meetMenu가 존재하는지 확인
        Optional<Meet> existingMeet = meetRepository.findByRoomIdAndMeetMenu(chatRoom.getId(), maxVotedMenu);
        if (existingMeet.isPresent()) {
            log.info("해당 메뉴가 이미 채팅방에 등록되어 있습니다. 기존 메뉴를 반환합니다.");
            Meet meet = existingMeet.get();
            return MeetResponseDTO.builder()
                    .meetId(meet.getId())
                    .maxVotedMenu(maxVotedMenu)
                    .build();
        }

        // 새로운 meet 생성 및 저장
        Meet meet = new Meet();
        meet.setMeetMenu(maxVotedMenu);
        meet = meetRepository.save(meet);
        log.info("새로운 meet 객체가 저장되었습니다. meetId: {}", meet.getId());

        // 채팅방에 meet 추가 및 저장
        chatRoom.addMeet(meet);
        chatRoom = chatRoomRepository.save(chatRoom);
        log.info("chatRoom 객체가 저장되었습니다. chatRoomId: {}, meetId: {}", chatRoom.getId(), meet.getId());

        // 새로운 채팅 생성 및 저장
        Chat chat = Chat.createChat(chatRoom, null, meet, SecurityUtil.getLoginId());
        chatRepository.save(chat);
        log.info("새로운 chat 객체가 저장되었습니다. chatId: {}", chat.getId());

        // 음식 정보 업데이트
        Food findFood = foodService.findByFoodName(maxVotedMenu)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        findFood.incrementFoodCount();
        foodRepository.save(findFood);
        log.info("Food 객체가 업데이트되었습니다. foodName: {}, count: {}", findFood.getFoodName(), findFood.getCount());

        // 결과 반환
        return MeetResponseDTO.builder()
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
