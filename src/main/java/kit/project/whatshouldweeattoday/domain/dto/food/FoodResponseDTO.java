package kit.project.whatshouldweeattoday.domain.dto.food;

import lombok.Getter;

@Getter
public class FoodResponseDTO {
    private Long id;
    private String foodName;
    private int count=0;//순위를 정하기 위한 변수
}
