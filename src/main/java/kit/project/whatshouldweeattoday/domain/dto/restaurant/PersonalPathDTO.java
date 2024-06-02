package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import com.fasterxml.jackson.databind.JsonNode;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalPathDTO {
   // private Restaurant restaurant;
    private RestaurantResponseDTO restaurantResponseDTO;
   /* private String RestaurantName;
    private Double RestaurantScore;*/
    private Integer totalTime;
    private Integer serialNum;
    private Integer weight = 0; //가중치
    private JsonNode routeInfo; //경로
    public PersonalPathDTO(RestaurantResponseDTO restaurant, Integer totalTime, Integer serialNum) {
        this.restaurantResponseDTO = restaurant;
        this.totalTime = totalTime;
        this.serialNum = serialNum;
    }

    @Override
    public String toString() {
        return "PersonalPath{" +
                "restaurant=" + restaurantResponseDTO +
                ", totalTime=" + totalTime +
                ", serialNum=" + serialNum +
                ", weight=" + weight +
                '}';
    }
}