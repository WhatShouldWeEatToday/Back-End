package kit.project.whatshouldweeattoday.domain.dto.foodType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FoodTypeResponseDTO {
    private Long id;
    private String foodTypeName;
    private Long count;//순위를 정하기 위한 변수
    private Long ranks;

    //주간순위
    public FoodTypeResponseDTO(Long id, String foodTypeName, int count, int rank) {
        this.id=id;
        this.foodTypeName=foodTypeName;
        this.count= (long) count;
        this.ranks= (long) rank;
    }
}
