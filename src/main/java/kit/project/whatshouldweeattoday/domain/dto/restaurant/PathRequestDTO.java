package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PathRequestDTO {
    private String departure;
    private String destination;
    private List<String> destinations;
    private String searchDttm; //출발예정시각
}
