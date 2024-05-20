package kit.project.whatshouldweeattoday.domain.dto.meet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeetRequestDTO {
    private String meetLocate;
    private String meetMenu;
    private Date meetTime;
}
