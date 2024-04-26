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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendController {

    private final UserService userService;
    private final FriendService friendService;

    @PostMapping("/api/friend/add/{loginId}")
    public ResponseEntity<?> addFriend(@Valid @PathVariable("loginId") String loginId, HttpSession session) throws Exception {
        if(!userService.confirmId(loginId)) {
            throw new BadRequestException("존재하지 않는 사용자입니다.");
        }
        User loginMember = (User)session.getAttribute("loginMember");
        friendService.createFriendship(loginMember.getLoginId(), loginId);

        return ResponseEntity.ok(new MsgResponseDTO("친구 추가 완료", HttpStatus.OK.value()));
    }
}
