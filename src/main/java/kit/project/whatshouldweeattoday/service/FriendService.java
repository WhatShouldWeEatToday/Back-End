package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.friend.WaitingFriendListDTO;
import kit.project.whatshouldweeattoday.domain.entity.Friendship;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
import kit.project.whatshouldweeattoday.repository.FriendshipRepository;
import kit.project.whatshouldweeattoday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void createFriendship(String fromLoginId, String toLoginId) throws Exception {
        User fromUser = userRepository.findByLoginId(fromLoginId);
        User toUser = userRepository.findByLoginId(toLoginId);
        if(fromUser == null || toUser == null) {
            throw new BadRequestException("회원 조회를 실패하였습니다.");
        }

        // 받는 사람에게 저장될 친구 요청
        Friendship friendshipFrom = Friendship.builder()
                .user(fromUser)
                .userLoginId(fromLoginId)
                .friendLoginId(toLoginId)
                .status(FriendshipStatus.WAITING)
                .isFrom(true)
                .build();

        // 보내는 사람에게 저장될 친구 요청
        Friendship friendshipTo = Friendship.builder()
                .user(toUser)
                .userLoginId(toLoginId)
                .friendLoginId(fromLoginId)
                .status(FriendshipStatus.WAITING)
                .isFrom(false)
                .build();

        // 각각의 친구리스트에 저장
        fromUser.getFriendshipList().add(friendshipTo);
        toUser.getFriendshipList().add(friendshipFrom);

        // 저장을 먼저 해야 서로의 친구 요청 번호가 생성됨
        friendshipRepository.save(friendshipTo);
        friendshipRepository.save(friendshipFrom);

        // 매칭되는 친구요청의 아이디를 저장한다.
        friendshipTo.setCounterpartId(friendshipFrom.getId());
        friendshipFrom.setCounterpartId(friendshipTo.getId());
    }
}
