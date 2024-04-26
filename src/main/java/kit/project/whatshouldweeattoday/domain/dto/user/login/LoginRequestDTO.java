package kit.project.whatshouldweeattoday.domain.dto.user.login;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginRequestDTO {
    @NotEmpty
    private final String loginId;
    @NotEmpty
    private final String loginPw;

    @JsonCreator
    public LoginRequestDTO(@JsonProperty("loginId")String loginId, @JsonProperty("loginPw")String loginPw) {
        this.loginId = loginId;
        this.loginPw = loginPw;
    }
}