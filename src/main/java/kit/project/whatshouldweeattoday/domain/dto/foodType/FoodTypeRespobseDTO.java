package kit.project.whatshouldweeattoday.domain.dto.foodType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodTypeRespobseDTO {
    private Long id;
    private String foodTypeName;
    private Long count;//순위를 정하기 위한 변수
    private Long ranks;
}
