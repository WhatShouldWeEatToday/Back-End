package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Restaurant {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESTAURANT_ID")
    private Long id;
    private String name;
    private String restaurantType;
    private Double degree;
    private String addressRoad;
    private String addressNumber;
    private String tel;
    private String menus;

    private int totalReviews;
    private int totalTaste;
    private int totalCost;
    private int totalKind;
    private int totalMood;
    private int totalPark;

    private Double latitude;
    private Double longitude;

    //null값을 허용하기 위해
    private Integer pathTime;
    private Double distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;

    @OneToMany(mappedBy = "restaurant", orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();
}