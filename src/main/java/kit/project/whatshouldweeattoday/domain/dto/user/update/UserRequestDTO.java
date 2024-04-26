package kit.project.whatshouldweeattoday.domain.dto.user.update;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestDTO {

        private String nickname;
        private String gender;
        private int age;
}