package kit.project.whatshouldweeattoday.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kit.project.whatshouldweeattoday.domain.dto.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.friend.WaitingFriendListDTO;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.service.FriendService;
import kit.project.whatshouldweeattoday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class FriendController {

    private final UserService userService;
    private final FriendService friendService;

    @PostMapping("/friend/add/{loginId}")
    public ResponseEntity<?> addFriend(@Valid @PathVariable("loginId") String loginId, HttpSession session) throws Exception {
        if(!userService.confirmId(loginId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        User loginUser = (User)session.getAttribute("loginUser");
        friendService.createFriendship(loginUser.getLoginId(), loginId);

        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 완료", HttpStatus.OK.value()));
    }

    @GetMapping("/friend/add-list")
    public ResponseEntity<?> getWaitingFriendInfo(HttpSession session) throws Exception {
        User loginUser = (User)session.getAttribute("loginUser");
        List<WaitingFriendListDTO> waitingFriendList = friendService.getWaitingFriendList(loginUser.getLoginId());

        return new ResponseEntity<>(waitingFriendList, HttpStatus.OK);
    }

    @PostMapping("/chat/friend/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriend(@Valid @PathVariable("friendshipId") Long friendshipId) throws Exception {
        friendService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 수락", HttpStatus.OK.value()));
    }
}
