package kit.project.whatshouldweeattoday.domain.dto.vote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequestDTO {
    private List<String> menu;
    private Long voteCount;
}
