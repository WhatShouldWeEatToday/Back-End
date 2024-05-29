package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListDTO;
import kit.project.whatshouldweeattoday.domain.entity.Friendship;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
import kit.project.whatshouldweeattoday.repository.FriendshipRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {

    private final MemberRepository memberRepository;
    private final FriendshipRepository friendshipRepository;

    public void createFriendship(String toLoginId) throws BadRequestException {
        // 현재 로그인 되어있는 유저(보내는 사람)
        String fromLoginId = String.valueOf(SecurityUtil.getLoginId());
        if (fromLoginId == null) {
            throw new BadRequestException("에러 발생");
        }

        // 유저 정보를 모두 가져옴
        Member fromMember = memberRepository.findByLoginId(fromLoginId).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
        Member toMember = memberRepository.findByLoginId(toLoginId).orElseThrow(() -> new BadRequestException("회원 조회 실패"));

        // 받는 사람에게 저장될 친구 요청
        Friendship friendshipFrom = Friendship.builder()
                .member(fromMember)
                .memberLoginId(fromLoginId)
                .friendLoginId(toLoginId)
                .status(FriendshipStatus.WAITING)
                .isFrom(true)
                .build();

        // 보내는 사람에게 저장될 친구 요청
        Friendship friendshipTo = Friendship.builder()
                .member(toMember)
                .memberLoginId(toLoginId)
                .friendLoginId(fromLoginId)
                .status(FriendshipStatus.WAITING)
                .isFrom(false)
                .build();

        // 각각의 친구리스트에 저장
        fromMember.getFriendshipList().add(friendshipTo);
        toMember.getFriendshipList().add(friendshipFrom);

        // 저장을 먼저 해야 서로의 친구 요청 번호가 생성됨
        friendshipRepository.save(friendshipTo);
        friendshipRepository.save(friendshipFrom);

        // 매칭되는 친구요청의 아이디를 저장한다.
        friendshipTo.setCounterpartId(friendshipFrom.getId());
        friendshipFrom.setCounterpartId(friendshipTo.getId());
    }

    public List<FriendListDTO> getFriendList() throws BadRequestException {
        // 현재 로그인 되어있는 유저(보내는 사람)
        Member user = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
        List<Friendship> friendshipList = user.getFriendshipList();
        // 조회된 결과 객체를 담을 DTO 리스트
        List<FriendListDTO> result = new ArrayList<>();

        for (Friendship request : friendshipList) {
            // 친구 추가 요청이 수락된 목록만 조회
            if(request.getStatus() == FriendshipStatus.ACCEPT) {
                Member friend = memberRepository.findByLoginId(request.getFriendLoginId()).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
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

    public List<FriendListDTO> getWaitingFriendList() throws Exception {
        // 현재 로그인 되어있는 유저(보내는 사람)
        Member user = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
        List<Friendship> friendshipList = user.getFriendshipList();
        // 조회된 결과 객체를 담을 DTO 리스트
        List<FriendListDTO> result = new ArrayList<>();

        for (Friendship request : friendshipList) {
            if(!request.isFrom() && (request.getStatus() == FriendshipStatus.WAITING)) {
                Member friend = memberRepository.findByLoginId(request.getFriendLoginId()).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
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

    public void acceptFriendRequest(Long friendshipId) throws Exception {
        // 누를 친구 요청과 매칭되는 상대방 친구 요청 둘 다 가져옴
        Friendship friendship = friendshipRepository.findById(friendshipId).orElseThrow(() -> new BadRequestException("친구 요청 조회 실패"));
        Friendship counterFriendship = friendshipRepository.findById(friendship.getCounterpartId()).orElseThrow(() -> new BadRequestException("친구 요청 조회 실패"));

        // 둘다 상태를 ACCEPT 로 변경함
        friendship.acceptFriendshipRequest();
        counterFriendship.acceptFriendshipRequest();
    }

    public void cancelFriendRequest(Long friendshipId) {
        friendshipRepository.deleteById(friendshipId);
    }
}
