package kit.project.whatshouldweeattoday.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Bookmark {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOKMARK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Builder
    public Bookmark(Member member, Restaurant restaurant) {
        this.member = member;
        this.restaurant = restaurant;
    }
   /* public void setRestaurants(Restaurant restaurant) {
        if (restaurant != null) {
            if (restaurants == null) {
                restaurants = new ArrayList<>();
            }
            restaurants.add(restaurant); // 리스트에 Restaurant 객체 추가
        }
    }*/

}
