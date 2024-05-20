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
    private int totalTime; // 총 소요시간
    private int transferCount; // 환승횟수
    private int totalWalkDistance; // 총 보행자 이동거리
    private int totalDistance; // 총 이동거리
    private int totalWalkTime; // 총 보행자 소요시간(sec)
    private int totalFare; // 총 교통요금
    private String mode; // 이동수단 종류
    private List<List<Double>> polyline; // 경로 좌표 리스트
}