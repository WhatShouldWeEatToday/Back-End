package kit.project.whatshouldweeattoday.domain.dto.rank;

import kit.project.whatshouldweeattoday.domain.dto.food.FoodResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class WeeklyFoodRankResponseDTO {
    private Long id;
    private int ranks;
    private String foodTypeName;
    private String date;
    private List<FoodResponseDTO> topFoods;

    public void setDate(String date) {
        this.date = date;
    }

    public WeeklyFoodRankResponseDTO(String date, List<FoodResponseDTO> topFoods) {
        this.date = date;
        this.topFoods = topFoods;
    }
}
