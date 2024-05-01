package kit.project.whatshouldweeattoday.domain.dto.bookmark;

import kit.project.whatshouldweeattoday.domain.entity.Bookmark;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class BookmarkRequestDTO {
    private Restaurant restaurant;

    @Builder
    public BookmarkRequestDTO(Restaurant restaurant){
        this.restaurant=restaurant;
    }
    public Bookmark toSaveEntity(){
        System.out.println("bookmark 테이블에 맛집저장");
        return Bookmark.builder()
                .restaurant(restaurant)
                .build();
    }
}
