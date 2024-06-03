package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_ID")
    private Long id;
    private String foodName;
    //private String restaurantType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "foodType_id")
    private FoodType foodType;
    private String imageRoute;
    private Long count; //채팅방에서 나올때마다 count 됨

    @OneToOne(mappedBy = "food", fetch = FetchType.LAZY)
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_rank_id")
    private WeeklyFoodRank weeklyFoodRank;

    public void setCount(Long count) {
        this.count = count;
    }
    public void setImageRoute(String imageRoute){this.imageRoute=imageRoute;}
}
