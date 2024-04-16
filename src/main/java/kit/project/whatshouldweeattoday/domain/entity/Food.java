package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Food {

    @Id
    @GeneratedValue
    @Column(name = "FOOD_ID")
    private Long id;
    private String restaurantType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_rank_id")
    private WeeklyFoodRank weeklyFoodRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_type_rank_id")
    private WeeklyFoodTypeRank weeklyFoodTypeRank;
}
