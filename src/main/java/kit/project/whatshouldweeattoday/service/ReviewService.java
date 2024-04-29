package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Review;
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

    //리뷰 등록
    @Transactional
    public ReviewResponseDTO save(ReviewRequestDTO requestDTO) {
        Review review = new Review(requestDTO);
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
}
