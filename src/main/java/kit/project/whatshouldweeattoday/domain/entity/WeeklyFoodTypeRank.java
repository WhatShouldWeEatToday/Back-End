package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class WeeklyFoodTypeRank extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOOD_TYPE_RANK_ID")
    private Long id;
    private int ranks;
    private String foodTypeName;

    @OneToMany(mappedBy = "weeklyFoodTypeRank")
    private List<Food> foods = new ArrayList<>();
}
