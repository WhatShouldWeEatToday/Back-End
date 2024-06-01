package kit.project.whatshouldweeattoday.controller;

import jakarta.validation.Valid;
import kit.project.whatshouldweeattoday.domain.dto.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListResponseDTO;
import kit.project.whatshouldweeattoday.service.FriendshipService;
import kit.project.whatshouldweeattoday.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class FriendshipController {

    private final MemberService memberService;
    private final FriendshipService friendshipService;

    /* 친구 검색 */
    @GetMapping("/friend/search")
    public ResponseEntity<Page<FriendListResponseDTO>> searchFriend(@RequestParam(name = "loginId") String loginId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) throws Exception {
        if(!memberService.confirmId(loginId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        Page<FriendListResponseDTO> responseDTOS = memberService.searchByLoginId(loginId, pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    /* 친구 추가 요청 */
    @PostMapping("/friend/add/{loginId}")
    public ResponseEntity<MsgResponseDTO> addFriend(@Valid @PathVariable("loginId") String loginId) throws Exception {
        if(!memberService.confirmId(loginId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        friendshipService.createFriendship(loginId);

        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 완료", HttpStatus.OK.value()));
    }

    /* 친구 목록 조회 */
    @GetMapping("/friend-list")
    public ResponseEntity<?> getFriendInfo() throws Exception {
        List<FriendListDTO> friendList = friendshipService.getFriendList();

        return new ResponseEntity<>(friendList, HttpStatus.OK);
    }

    /* 친구 추가 요청 조회 */
    @GetMapping("/friend-add-list")
    public ResponseEntity<?> getWaitingFriendInfo() throws Exception {
        List<FriendListDTO> waitingFriendList = friendshipService.getWaitingFriendList();

        return new ResponseEntity<>(waitingFriendList, HttpStatus.OK);
    }

    /* 친구 추가 수락 */
    @PostMapping("/friend/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriend(@Valid @PathVariable("friendshipId") Long friendshipId) throws Exception {
        friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 수락", HttpStatus.OK.value()));
    }

    /* 친구 추가 취소 */
    @DeleteMapping("/friend/cancel/{friendshipId}")
    public ResponseEntity<?> cancelFriend(@Valid @PathVariable("friendshipId") Long friendshipId) {
        friendshipService.cancelFriendRequest(friendshipId);
        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 취소", HttpStatus.OK.value()));
    }
}
