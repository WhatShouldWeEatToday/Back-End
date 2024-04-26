package kit.project.whatshouldweeattoday.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kit.project.whatshouldweeattoday.domain.dto.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.login.LoginRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.update.UserRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.update.UserResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.service.UserService;
import kit.project.whatshouldweeattoday.service.util.SessionConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO requestDTO) {
        log.info("Received signup request: {}", requestDTO.getLoginId());
        userService.createUser(requestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/confirmLoginId/{loginId}")
    public ResponseEntity<String> confirmId(@PathVariable("loginId") String loginId) throws BadRequestException {
        if(userService.confirmId(loginId)) {
            throw new BadRequestException("이미 사용중인 아이디입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 아이디 입니다.");
        }
    }

    @GetMapping("/confirmNickname/{nickname}")
    public ResponseEntity<String> confirmNickname(@PathVariable("nickname") String nickname) throws BadRequestException {
        if(userService.confirmNickname(nickname)) {
            throw new BadRequestException("이미 사용중인 닉네임입니다.");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임 입니다.");
        }
    }

    @GetMapping("/mypage/update")
    public ResponseEntity<UserResponseDTO>  update(HttpSession session) {
        session.removeAttribute("msg");
        String loginId = (String) session.getAttribute("loginId");
        User user = userService.findByLoginId(loginId);
        UserResponseDTO responseDTO = new UserResponseDTO(user);

        UserResponseDTO.builder()
                .nickname(responseDTO.getNickname())
                .gender(responseDTO.getGender())
                .age(responseDTO.getAge())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PatchMapping("/api/user/update/{loginId}")
    public ResponseEntity<MsgResponseDTO> updateUser(@PathVariable("loginId") String loginId, @RequestBody UserRequestDTO userRequestDTO) {
        userService.updateUser(loginId, userRequestDTO);
        return ResponseEntity.ok(new MsgResponseDTO("회원 정보 수정 완료", HttpStatus.OK.value()));
    }

    @DeleteMapping("/api/user/delete/{loginId}")
    public String deleteUser(@PathVariable("loginId") String loginId, HttpSession session) {
        userService.deleteUser(loginId);
        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("/api/signin")
    public ResponseEntity<MsgResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDto, HttpServletRequest request) {
        String loginId = loginRequestDto.getLoginId();
        String loginPw = loginRequestDto.getLoginPw();

        User user = userService.findByLoginId(loginRequestDto.getLoginId());
        if (user == null) {
            try {
                throw new IllegalArgumentException();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.ok(new MsgResponseDTO("존재하지 않는 회원입니다.", HttpStatus.FORBIDDEN.value()));
            }
        }
        userService.login(loginId, loginPw);

        getSession(request, user);
        return ResponseEntity.ok(new MsgResponseDTO("로그인 완료", HttpStatus.OK.value()));
    }

    private static void getSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, user); // 세션에 로그인 회원 정보 보관
    }

    @PostMapping("/api/signout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
