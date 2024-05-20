package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PathResponseStepInfoDTO {
    private String description;
    private List<Double> coordinates;
}
