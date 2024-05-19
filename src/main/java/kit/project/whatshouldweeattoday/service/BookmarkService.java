package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.bookmark.BookmarkRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Bookmark;
import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.BookmarkRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final RestaurantRepository restaurantRepository;


    //즐겨찾기 등록
    @Transactional
    public void save(Long restaurantId, BookmarkRequestDTO bookmarkRequestDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(RuntimeException::new);
        Bookmark bookmark = bookmarkRequestDTO.toSaveEntity();
        bookmark.setRestaurants(restaurant);

        bookmarkRepository.save(bookmark);
    }

    //즐겨찾기 삭제
    @Transactional
    public MsgResponseDTO delete(Long restaurantId, Long bookmarkId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(RuntimeException::new);
        bookmarkRepository.deleteById(bookmarkId);
        return new MsgResponseDTO("즐겨찾기 취소", 200);
    }
}
