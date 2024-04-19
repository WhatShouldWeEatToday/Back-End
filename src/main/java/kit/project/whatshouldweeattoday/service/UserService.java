package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.UserDTO;
import kit.project.whatshouldweeattoday.domain.entity.User;
import kit.project.whatshouldweeattoday.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void createUser(SignupRequestDTO signupRequestDTO) {
        String loginId = signupRequestDTO.getLoginId();
        String loginPw = signupRequestDTO.getLoginPw();
        String nickname = signupRequestDTO.getNickname();
        int gender = signupRequestDTO.getGender();
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
}
