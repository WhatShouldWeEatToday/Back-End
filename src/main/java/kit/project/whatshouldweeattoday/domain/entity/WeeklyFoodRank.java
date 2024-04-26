package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class WeeklyFoodRank extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_RANK_ID")
    private Long id;
    private int ranks;
    private String foodName;

    @OneToMany(mappedBy = "weeklyFoodRank")
    private List<Food> foods = new ArrayList<>();
}
