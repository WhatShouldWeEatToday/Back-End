//package kit.project.whatshouldweeattoday.service;
//
//import com.auth0.jwt.interfaces.DecodedJWT;
//import jakarta.persistence.EntityManager;
//import kit.project.whatshouldweeattoday.domain.entity.Friendship;
//import kit.project.whatshouldweeattoday.domain.entity.Member;
//import kit.project.whatshouldweeattoday.domain.type.FriendshipStatus;
//import kit.project.whatshouldweeattoday.repository.FriendshipRepository;
//import kit.project.whatshouldweeattoday.repository.MemberRepository;
//import kit.project.whatshouldweeattoday.repository.MemberRepository;
//import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
//import org.apache.coyote.BadRequestException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest
//@Transactional
//class FriendshipServiceTest {
//
//    @Autowired MemberRepository memberRepository;
//    @Autowired FriendshipRepository friendshipRepository;
//    @Autowired FriendshipService friendshipService;
//    @Autowired EntityManager em;
//
//    private String toLoginId = "lim3478";
////    private String fromloginId = "hyun3478";
//
//    @Test
//    @DisplayName("친구_추가_요청")
//    public void create_Friendship() throws BadRequestException {
//        String fromLoginId = String.valueOf(SecurityUtil.getLoginId());
//        if (fromLoginId == null) {
//            throw new BadRequestException("에러 발생");
//        }
//
//        // 유저 정보를 모두 가져옴
//        Member fromMember = memberRepository.findByLoginId(fromLoginId).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
//        Member toMember = memberRepository.findByLoginId(toLoginId).orElseThrow(() -> new BadRequestException("회원 조회 실패"));
//
//        // 받는 사람에게 저장될 친구 요청
//        Friendship friendshipFrom = Friendship.builder()
//                .member(fromMember)
//                .memberLoginId(fromLoginId)
//                .friendLoginId(toLoginId)
//                .status(FriendshipStatus.WAITING)
//                .isFrom(true)
//                .build();
//
//        // 보내는 사람에게 저장될 친구 요청
//        Friendship friendshipTo = Friendship.builder()
//                .member(toMember)
//                .memberLoginId(toLoginId)
//                .friendLoginId(fromLoginId)
//                .status(FriendshipStatus.WAITING)
//                .isFrom(false)
//                .build();
//
//        // 각각의 친구리스트에 저장
//        fromMember.getFriendshipList().add(friendshipTo);
//        toMember.getFriendshipList().add(friendshipFrom);
//
//        // 저장을 먼저 해야 서로의 친구 요청 번호가 생성됨
//        friendshipRepository.save(friendshipTo);
//        friendshipRepository.save(friendshipFrom);
//
//        // 매칭되는 친구요청의 아이디를 저장한다.
//        friendshipTo.setCounterpartId(friendshipFrom.getId());
//        friendshipFrom.setCounterpartId(friendshipTo.getId());
//        // given
//
//
//        // when
//
//
//        // then
//
//    }
//
//    @Test
//    @DisplayName("친구_목록_조회")
//    public void get_Friend_Info(){
//        // given
//
//
//        // when
//
//
//        // then
//
//    }
//
//    @Test
//    @DisplayName("친구_추가_요청_조회")
//    public void get_Waiting_Friend_Info(){
//        // given
//
//
//        // when
//
//
//        // then
//
//    }
//
//    @Test
//    @DisplayName("친구_추가_수락")
//    public void accept_Friend(){
//        // given
//
//
//        // when
//
//
//        // then
//
//    }
//
//    @Test
//    @DisplayName("친구_추가_취소")
//    public void cancel_Friend(){
//        // given
//
//
//        // when
//
//
//        // then
//
//    }
//}