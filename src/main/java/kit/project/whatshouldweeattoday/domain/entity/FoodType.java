package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FoodType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOODTYPE_ID")
    private Long id;
    private String foodTypeName;
    private Long count;
    @OneToMany(mappedBy = "foodType",fetch = FetchType.LAZY)
    private List<Restaurant>  restaurants;
}
