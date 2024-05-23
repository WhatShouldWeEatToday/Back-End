package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalPath {
    private Restaurant restaurant;
    private Integer totalTime;
    private Integer serialNum;
    private Integer weight = 0; //가중치

    public PersonalPath(Restaurant restaurant, Integer totalTime, Integer serialNum) {
        this.restaurant = restaurant;
        this.totalTime = totalTime;
        this.serialNum = serialNum;
    }

    @Override
    public String toString() {
        return "PersonalPath{" +
                "restaurant=" + restaurant +
                ", totalTime=" + totalTime +
                ", serialNum=" + serialNum +
                ", weight=" + weight +
                '}';
    }
}