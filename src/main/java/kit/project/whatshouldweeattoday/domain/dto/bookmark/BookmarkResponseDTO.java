package kit.project.whatshouldweeattoday.domain.dto.bookmark;

import kit.project.whatshouldweeattoday.domain.entity.Bookmark;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BookmarkResponseDTO {
    private Long id;
    private Restaurant restaurant;
    private Long restaurantId;
    private String restaurantTel;
    private String restaurantName;
    private String addressRoad;
    private Double degrees;
    private int reviews;
    private Long member_id;

    public BookmarkResponseDTO(Bookmark bookmark){
        this.member_id=bookmark.getMember().getId();
        this.id = bookmark.getId();
        this.restaurantId = bookmark.getRestaurant().getId();
        this.restaurantTel=bookmark.getRestaurant().getTel();
        this.restaurantName = bookmark.getRestaurant().getName();
        this.addressRoad=bookmark.getRestaurant().getAddressRoad();
        this.degrees=bookmark.getRestaurant().getDegree();
        this.reviews=bookmark.getRestaurant().getTotalReviews();
    }
}
