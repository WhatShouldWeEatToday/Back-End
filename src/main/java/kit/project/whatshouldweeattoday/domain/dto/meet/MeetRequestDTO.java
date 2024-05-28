package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetRequestDTO {
    private String meetLocate;
    private LocalDateTime meetTime;
}
