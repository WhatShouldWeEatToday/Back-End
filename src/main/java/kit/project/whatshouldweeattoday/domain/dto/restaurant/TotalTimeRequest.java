package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotalTimeRequest {
    private String departure;
    private String destination;
    private String startX;
    private String startY;
    private String endX;
    private String endY;
    private int lang;
    private String format;
    private int count;
    private String searchDttm; //출발예정시각
}
