package kit.project.whatshouldweeattoday.domain.dto.bookmark;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Bookmark;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Getter;

@Getter
public class BookmarkResponseDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantTel;
    private String restaurantName;
    private String addressRoad;
    private Double degrees;
    private int reviews;
    private Long member_id;

    public BookmarkResponseDTO(Bookmark bookmark){
        RestaurantResponseDTO restaurantResponseDTO= mapToRestaurantResponseDTO(bookmark.getRestaurant());
        this.member_id=bookmark.getMember().getId();
        this.id = bookmark.getId();
        this.restaurantId = restaurantResponseDTO.getId();
        this.restaurantTel=restaurantResponseDTO.getTel();
        this.restaurantName =restaurantResponseDTO.getName();
        this.addressRoad=restaurantResponseDTO.getAddressRoad();
        this.degrees=restaurantResponseDTO.getDegree();
        this.reviews=restaurantResponseDTO.getTotalReviews();
    }
    private RestaurantResponseDTO mapToRestaurantResponseDTO(Restaurant restaurant) {
        RestaurantResponseDTO dto = new RestaurantResponseDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setTel(restaurant.getTel());
        dto.setAddressRoad(restaurant.getAddressRoad());
        dto.setDegree(restaurant.getDegree());
        return dto;
    }
}
