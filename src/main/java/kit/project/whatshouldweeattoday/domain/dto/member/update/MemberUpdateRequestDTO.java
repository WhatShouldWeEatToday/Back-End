package kit.project.whatshouldweeattoday.domain.dto.member.update;

import java.util.Optional;

public record MemberUpdateRequestDTO(Optional<String> nickname, Optional<String> gender,
                                    Optional<Integer> age) {
}