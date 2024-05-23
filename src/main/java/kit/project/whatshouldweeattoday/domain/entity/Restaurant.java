package kit.project.whatshouldweeattoday.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    // null값을 허용하기 위해
    private Integer pathTime;
    private Double distance;
    private int count; // 음식종류별 순위

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookmark_id")
    private Bookmark bookmark;

    @OneToMany(mappedBy = "restaurant", fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviewList = new ArrayList<>();

    public void setCoordinates(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        System.out.println("setCoordinates의 경도 위도 " + longitude + " " + latitude);
    }

    public void calculateDegree(Double newDegree) {
        if (totalReviews == 0) {
            degree = newDegree;
        } else {
            degree = ((degree * totalReviews) + newDegree) / (totalReviews + 1);
        }
        // 소수 첫째 자리까지
        degree = Math.round(degree * 10) / 10.0;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", degree=" + degree +
                ", addressRoad='" + addressRoad + '\'' +
                ", addressNumber='" + addressNumber + '\'' +
                ", pathTime=" + pathTime +
                '}';
    }
}