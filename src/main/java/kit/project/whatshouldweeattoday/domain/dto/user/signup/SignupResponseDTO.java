package kit.project.whatshouldweeattoday.domain.dto.user.signup;

import kit.project.whatshouldweeattoday.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupResponseDTO {
    private final Long id;
    private final String loginId;
    private final String loginPw;
    private final String nickname;
    private final String gender;
    private final int age;

    @Builder
    public SignupResponseDTO(Long id, String loginId, String loginPw, String nickname, String gender, int age) {
        this.id = id;
        this.loginId = loginId;
        this.loginPw = loginPw;
        this.nickname = nickname;
        this.gender = gender;
        this.age = age;
    }

    /* Entity -> DTO */
    public SignupResponseDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.loginPw = user.getLoginPw();
        this.nickname = user.getNickname();
        this.gender = user.getGender();
        this.age = user.getAge();
    }
}
