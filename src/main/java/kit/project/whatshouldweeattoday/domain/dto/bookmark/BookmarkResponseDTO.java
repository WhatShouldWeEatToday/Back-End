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
    private Long member_id;

    public BookmarkResponseDTO(Bookmark bookmark){
        this.id = bookmark.getId();
        this.restaurant = bookmark.getRestaurant();
    }
}
