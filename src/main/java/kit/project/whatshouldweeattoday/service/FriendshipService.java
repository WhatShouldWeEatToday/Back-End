package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListDTO;
import kit.project.whatshouldweeattoday.domain.entity.Friendship;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
import kit.project.whatshouldweeattoday.repository.FriendshipRepository;
import kit.project.whatshouldweeattoday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public Page<FriendListResponseDTO> searchByLoginId(String keyword, Pageable pageable) {
        return friendshipRepository.findByFriendLoginIdContaining(keyword, pageable);
    }

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

    @Transactional
    public List<FriendListDTO> getFriendList(String loginId) throws BadRequestException {
        User user = userRepository.findByLoginId(loginId);
        List<Friendship> friendshipList = user.getFriendshipList();
        List<FriendListDTO> result = new ArrayList<>();

        for (Friendship request : friendshipList) {
            if(!request.isFrom() && (request.getStatus() == FriendshipStatus.ACCEPT)) {
                User friend = userRepository.findByLoginId(request.getFriendLoginId());
                if(friend == null) {
                    throw new BadRequestException("회원 조회를 실패하였습니다.");
                }
                FriendListDTO friendList = FriendListDTO.builder()
                        .friendshipId(request.getId())
                        .friendLoginId(friend.getLoginId())
                        .friendNickname(friend.getNickname())
                        .status(request.getStatus())
                        .build();
                result.add(friendList);
            }
        }
        return result;
    }

    @Transactional
    public List<FriendListDTO> getWaitingFriendList(String loginId) throws Exception {
        User user = userRepository.findByLoginId(loginId);
        List<Friendship> friendshipList = user.getFriendshipList();
        List<FriendListDTO> result = new ArrayList<>();

        for (Friendship request : friendshipList) {
            if(!request.isFrom() && (request.getStatus() == FriendshipStatus.WAITING)) {
                User friend = userRepository.findByLoginId(request.getFriendLoginId());
                if(friend == null) {
                    throw new BadRequestException("회원 조회를 실패하였습니다.");
                }
                FriendListDTO waitingFriendList = FriendListDTO.builder()
                        .friendshipId(request.getId())
                        .friendLoginId(friend.getLoginId())
                        .friendNickname(friend.getNickname())
                        .status(request.getStatus())
                        .build();
                result.add(waitingFriendList);
            }
        }
        return result;
    }

    @Transactional
    public void acceptFriendRequest(Long friendshipId) throws Exception {
        // 누를 친구 요청과 매칭되는 상대방 친구 요청 둘다 가져옴
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new BadRequestException("친구 요청 조회 실패"));
        Friendship counterFriendship = friendshipRepository.findById(friendship.getCounterpartId()).orElseThrow(() -> new BadRequestException("친구 요청 조회 실패"));

        // 둘다 상태를 ACCEPT 로 변경함
        friendship.acceptFriendshipRequest();
        counterFriendship.acceptFriendshipRequest();
    }

    @Transactional
    public void cancelFriendRequest(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
}
