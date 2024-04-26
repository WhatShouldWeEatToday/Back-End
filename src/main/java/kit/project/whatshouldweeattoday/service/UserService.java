package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.user.login.LoginResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.update.UserRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.update.UserResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createUser(SignupRequestDTO signupRequestDTO) {
        String loginId = signupRequestDTO.getLoginId();
        String loginPw = signupRequestDTO.getLoginPw();
        String nickname = signupRequestDTO.getNickname();
        String gender = signupRequestDTO.getGender();
        int age = signupRequestDTO.getAge();

        User savedUser = new User(loginId, loginPw, nickname, gender, age);
        userRepository.save(savedUser);
    }

    @Transactional
    public boolean confirmId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Transactional
    public boolean confirmNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public LoginResponseDTO login(String loginId, String loginPw) {
        User user = userRepository.findByLoginId(loginId);

        // 비밀번호 일치 여부 확인
        try {
            if (!(Objects.equals(loginPw, user.getLoginPw()))) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            new ResponseEntity<>("존재하지 않는 회원입니다.", HttpStatus.FORBIDDEN);
        }

        return new LoginResponseDTO(loginId, loginPw);
    }

    @Transactional
    public User findByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }
}
