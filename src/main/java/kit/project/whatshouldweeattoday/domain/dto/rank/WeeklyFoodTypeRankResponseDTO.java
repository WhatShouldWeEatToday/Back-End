package kit.project.whatshouldweeattoday.domain.dto.rank;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WeeklyFoodTypeRankResponseDTO {
    private Long id;
    private int ranks;
    private String foodTypeName;
    private String date;
    private List<RestaurantResponseDTO> topRestaurants;
    public WeeklyFoodTypeRankResponseDTO(String date, List<RestaurantResponseDTO> topRestaurants) {
        this.date = date;
        this.topRestaurants = topRestaurants;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setTopRestaurants(List<RestaurantResponseDTO> topRestaurants) {
        this.topRestaurants = topRestaurants;
    }
}
