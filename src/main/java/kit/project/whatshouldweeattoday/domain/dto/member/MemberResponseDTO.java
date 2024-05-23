package kit.project.whatshouldweeattoday.domain.dto.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberResponseDTO {
    private Long id;
    private String loginId;
    private String loginPw;
    private String nickname;
    private String gender;
    private int age;
}
