package kit.project.whatshouldweeattoday.domain.dto.food;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodResponseDTO {
    private Long id;
    private String foodName;
    private int count=0;//순위를 정하기 위한 변수
    private int ranks;

    // 주간 순위 -> 음식 이름만 반환
    public FoodResponseDTO(Long id, String foodName, int count, int ranks) {
        this.id = id;
        this.foodName = foodName;
        this.count = count;
        this.ranks=ranks;
    }

}
