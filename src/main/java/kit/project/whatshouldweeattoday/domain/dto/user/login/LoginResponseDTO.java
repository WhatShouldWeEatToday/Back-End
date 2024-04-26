package kit.project.whatshouldweeattoday.domain.dto.user.login;

import kit.project.whatshouldweeattoday.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDTO {

        private final String loginId;
        private final String loginPw;

    @Builder
    public LoginResponseDTO(String loginId, String loginPw) {
        this.loginId = loginId;
        this.loginPw = loginPw;
    }

    /* Entity -> DTO */
    public LoginResponseDTO(User user) {
        this.loginId = user.getLoginId();
        this.loginPw = user.getLoginPw();
    }
}