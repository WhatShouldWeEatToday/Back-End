package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Bookmark;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.repository.UserRepository;
import kit.project.whatshouldweeattoday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO requestDTO) {
        userService.createUser(requestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/api/confirmId")
    public ResponseEntity<Boolean> confirmId(@RequestBody String loginId) {
        return ResponseEntity.ok(userService.confirmId(loginId));
    }

    @PostMapping("/api/confirmNickname")
    public ResponseEntity<Boolean> confirmNickname(@RequestBody String nickname) {
        return ResponseEntity.ok(userService.confirmNickname(nickname));
    }
}
