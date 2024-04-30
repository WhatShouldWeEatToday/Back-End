package kit.project.whatshouldweeattoday.domain.dto.member.signup;

import kit.project.whatshouldweeattoday.domain.entity.Member;
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
    public SignupResponseDTO(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.loginPw = member.getLoginPw();
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.age = member.getAge();
    }
}
