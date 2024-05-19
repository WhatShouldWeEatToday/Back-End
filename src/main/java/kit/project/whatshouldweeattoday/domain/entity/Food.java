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
    private String restaurantType;
    private String imageRoute;
    private int count = 0;

    @OneToOne(mappedBy = "food", fetch = FetchType.LAZY)
    private Meet meet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_rank_id")
    private WeeklyFoodRank weeklyFoodRank;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_type_rank_id")
    private WeeklyFoodTypeRank weeklyFoodTypeRank;*/
}
