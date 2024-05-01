package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Bookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOKMARK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "bookmark", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Restaurant> restaurants = new ArrayList<>();

    public void setRestaurants(Restaurant restaurant) {
        if (restaurant != null) {
            if (restaurants == null) {
                restaurants = new ArrayList<>();
            }
            restaurants.add(restaurant); // 리스트에 Restaurant 객체 추가
        }
    }
    @Builder
    public Bookmark(Restaurant restaurant) {
        this.restaurants.add(restaurant);
    }
}
