package kit.project.whatshouldweeattoday.domain.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteIdRequestDTO {

    public String menu1;
    public String menu2;
    private Long voteCount1;
    private Long voteCount2;
}
