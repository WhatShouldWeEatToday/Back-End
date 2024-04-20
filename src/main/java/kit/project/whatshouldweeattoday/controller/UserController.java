package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupResponseDTO;
import kit.project.whatshouldweeattoday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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

    @GetMapping("/confirmLoginId")
    public ResponseEntity<String> confirmId(@RequestParam String loginId) throws BadRequestException {
        if(userService.confirmId(loginId)) {
            throw new BadRequestException("이미 사용중인 아이디입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디 입니다.");
        }
    }

    @GetMapping("/confirmNickname")
    public ResponseEntity<String> confirmNickname(@RequestParam String nickname) throws BadRequestException {
        if(userService.confirmNickname(nickname)) {
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임 입니다.");
        }
    }
}
