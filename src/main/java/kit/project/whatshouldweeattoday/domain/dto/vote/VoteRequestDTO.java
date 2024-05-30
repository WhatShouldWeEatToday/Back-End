package kit.project.whatshouldweeattoday.domain.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequestDTO {

    private String menu1;
    private String menu2;
}
