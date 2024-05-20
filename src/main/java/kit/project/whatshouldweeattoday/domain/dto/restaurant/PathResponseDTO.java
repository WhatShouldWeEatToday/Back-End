package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PathResponseDTO {
    private String departure;
    private String destination;
    private int totalTime;
    private int transferCount;
    private int totalWalkDistance;
    private int totalDistance;
    private int totalWalkTime;
    private int totalFare;
    private String mode;
    private List<List<Double>> polyline;
    private List<PathResponseStepInfoDTO> steps;
}

