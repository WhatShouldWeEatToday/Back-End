package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue
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

    private double latitude;
    private double longitude;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;

    @OneToMany(mappedBy = "restaurant", orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();


}