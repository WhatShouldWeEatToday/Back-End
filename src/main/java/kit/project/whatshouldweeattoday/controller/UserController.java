package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupResponseDTO;
import kit.project.whatshouldweeattoday.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody SignupRequestDTO requestDTO) {
        userService.createUser(requestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
