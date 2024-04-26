package kit.project.whatshouldweeattoday.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kit.project.whatshouldweeattoday.domain.dto.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.FriendListDTO;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.service.FriendshipService;
import kit.project.whatshouldweeattoday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class FriendshipController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    @GetMapping("/friend/search")
    public ResponseEntity<Page<FriendListResponseDTO>> searchFriend(String friendId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 15) Pageable pageable) throws Exception {
        if(!userService.confirmId(friendId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        Page<FriendListResponseDTO> responseDTOS = friendshipService.searchByLoginId(friendId, pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    @PostMapping("/friend/add/{loginId}")
    public ResponseEntity<?> addFriend(@Valid @PathVariable("loginId") String loginId, HttpSession session) throws Exception {
        if(!userService.confirmId(loginId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        User loginUser = (User)session.getAttribute("loginUser");
        friendshipService.createFriendship(loginUser.getLoginId(), loginId);

        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 완료", HttpStatus.OK.value()));
    }

    @GetMapping("/friend-list")
    public ResponseEntity<?> getFriendInfo(HttpSession session) throws Exception {
        User loginUser = (User)session.getAttribute("loginUser");
        List<FriendListDTO> friendList = friendshipService.getFriendList(loginUser.getLoginId());

        return new ResponseEntity<>(friendList, HttpStatus.OK);
    }

    @GetMapping("/friend-add-list")
    public ResponseEntity<?> getWaitingFriendInfo(HttpSession session) throws Exception {
        User loginUser = (User)session.getAttribute("loginUser");
        List<FriendListDTO> waitingFriendList = friendshipService.getWaitingFriendList(loginUser.getLoginId());

        return new ResponseEntity<>(waitingFriendList, HttpStatus.OK);
    }

    @PostMapping("/chat/friend/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriend(@Valid @PathVariable("friendshipId") Long friendshipId) throws Exception {
        friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 수락", HttpStatus.OK.value()));
    }

    @DeleteMapping("/chat/friend/cancel/{friendshipId}")
    public ResponseEntity<?> cancelFriend(@Valid @PathVariable("friendshipId") Long friendshipId) {
        friendshipService.cancelFriendRequest(friendshipId);
        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 취소", HttpStatus.OK.value()));
    }
}
