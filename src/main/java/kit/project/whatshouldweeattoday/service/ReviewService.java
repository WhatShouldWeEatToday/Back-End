package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    //리뷰 등록
    @Transactional
    public ReviewResponseDTO save(Long restaurantId,ReviewRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(IllegalArgumentException::new);
        Review review = new Review(requestDTO);
        review.setRestaurant(restaurant);
        Review savedReview = reviewRepository.save(review);
        return new ReviewResponseDTO(savedReview);
    }

    //리뷰 전체조회
    @Transactional
    public Page<ReviewResponseDTO> findAll(Pageable pageable) {
        Page<Review> page = reviewRepository.findAll(pageable);

        Page<ReviewResponseDTO> dtoPage = page.map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO(review);
            return dto;
        });
        return dtoPage;
    }

    //리뷰 상세화면 및 수정
    @Transactional
    public ReviewResponseDTO reviewDetails(Long id) {
       Review review = reviewRepository.findById(id)
               .orElseThrow(IllegalArgumentException::new);
       return new ReviewResponseDTO(review);
    }
    //리뷰 수정
    @Transactional //얘 안붙이면 mysql에 수정데이터 안들어감
    public ReviewResponseDTO update(Long id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        review.updateReview(requestDTO.getCost(),requestDTO.getPark(),requestDTO.getMood(),requestDTO.getKind(),requestDTO.getTaste(),requestDTO.getStars());
        return new ReviewResponseDTO(review);
    }

    //리뷰 삭제
    @Transactional
    public MsgResponseDTO delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        reviewRepository.delete(review);
        return new MsgResponseDTO("리뷰 삭제 완료", 200);
    }

    public Page<ReviewResponseDTO> findByAdddress(String word, Pageable pageable) {
        Page<Review> page = reviewRepository.findByAddress(word, pageable);

        Page<ReviewResponseDTO> dtoPage = page.map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO(review);
            return dto;
        });
        return dtoPage;
    }
}
