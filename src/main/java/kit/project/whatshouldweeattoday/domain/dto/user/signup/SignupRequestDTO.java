package kit.project.whatshouldweeattoday.domain.dto.user.signup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequestDTO {
    @NotBlank
    @Size(min = 4,max = 10, message ="작성자명은 4자 이상 10자 이하만 가능합니다.")
    @Pattern(regexp = "^[a-z0-9]*$", message = "작성자명은 알파벳 소문자, 숫자만 사용 가능합니다.")
    private String loginId;

    @NotBlank
    @Size(min = 8,max = 15, message ="비밀번호는 8자 이상 15자 이하만 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z_0-9]*$", message = "비밀번호는 알파벳 대소문자, 숫자만 사용 가능합니다.")
    private String loginPw;
    private String verifiedLoginPw;

    @NotBlank
    private String nickname;
    private String gender;
    private int age;
}
