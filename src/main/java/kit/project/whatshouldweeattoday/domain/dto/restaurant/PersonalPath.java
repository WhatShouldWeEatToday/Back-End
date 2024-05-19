package kit.project.whatshouldweeattoday.domain.dto.restaurant;

import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PersonalPath {
    private Restaurant restaurant; // 음식점 id, 음식점 평점, 음식점 이름, 음식점 주소
    private int totalTime; // 소요시간
    private int serialNum; // 일련번호
    private int weight; // 가중치

    public PersonalPath(Restaurant restaurant, int totalTime, int serialNum){
        this.restaurant = restaurant;
        this.totalTime = totalTime;
        this.serialNum = serialNum;
    }
}