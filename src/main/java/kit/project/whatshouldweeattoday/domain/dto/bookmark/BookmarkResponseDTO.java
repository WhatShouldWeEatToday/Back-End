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
    private long id;
    private List<RestaurantResponseDTO> restaurants;

    @Builder
    public BookmarkResponseDTO(Long id, List<RestaurantResponseDTO> restaurants) {
        this.id = id;
        this.restaurants = restaurants;
    }

    public BookmarkResponseDTO(Bookmark bookmark){
        this.id = bookmark.getId();
        this.restaurants = bookmark.getRestaurants().stream()
                .map(RestaurantResponseDTO::new) // 각 Restaurant를 RestaurantResponseDTO로 변환
                .collect(Collectors.toList());
    }
}
