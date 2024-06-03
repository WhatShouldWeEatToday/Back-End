package kit.project.whatshouldweeattoday.domain.dto.rank;

import kit.project.whatshouldweeattoday.domain.dto.foodType.FoodTypeResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WeeklyFoodTypeRankResponseDTO {
    private Long id;
    private String date;
    private List<FoodTypeResponseDTO> topFoodTypes;
    public WeeklyFoodTypeRankResponseDTO(Long id, String date, List<FoodTypeResponseDTO> topFoodTypes) {
        this.id=id;
        this.date = date;
        this.topFoodTypes = topFoodTypes;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setTopRestaurants(List<FoodTypeResponseDTO> topFoodTypes) {
        this.topFoodTypes = topFoodTypes;
    }
}
