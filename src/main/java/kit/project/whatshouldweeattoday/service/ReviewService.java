package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public void save(Long id, ReviewRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        restaurant.setTotalReviews(restaurant.getTotalReviews() + 1);
        restaurant.setCount(restaurant.getCount() + 1);
        Review review = new Review(requestDTO);
        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        review.setWriter(member.getNickname());
        review.confirmMember(member);
        review.setRestaurant(restaurant);
        reviewRepository.save(review);
        updateRestaurantScores(restaurant, review, true);
        restaurant.caculateDegree(review.getStars());
    }

    private void updateRestaurantScores(Restaurant restaurant, Review review, boolean isAdding) {
        int delta = isAdding ? 1 : -1;
        if (review.getMood() == 1) {
            restaurant.setTotalMood(restaurant.getTotalMood() + delta);
        }
        if (review.getKind() == 1) {
            restaurant.setTotalKind(restaurant.getTotalKind() + delta);
        }
        if (review.getCost() == 1) {
            restaurant.setTotalCost(restaurant.getTotalCost() + delta);
        }
        if (review.getPark() == 1) {
            restaurant.setTotalPark(restaurant.getTotalPark() + delta);
        }
        if (review.getTaste() == 1) {
            restaurant.setTotalTaste(restaurant.getTotalTaste() + delta);
        }
    }

    @Transactional
    public Page<RestaurantResponseDTO> findAll(String address, Pageable pageable) {
        Page<Restaurant> pages = (address == null || address.trim().isEmpty())
                ? restaurantRepository.findAllByReviewCreated(pageable)
                : restaurantRepository.findByAddress(address, pageable);

        return pages.map(restaurant -> {
            Page<ReviewResponseDTO> reviewList = this.showReviewsByRestaurant(restaurant.getId(), PageRequest.of(0, 5));
            return new RestaurantResponseDTO(restaurant, reviewList);
        });
    }

    @Transactional
    public ReviewResponseDTO reviewDetails(Long restaurantId, Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return new ReviewResponseDTO(review);
    }

    @Transactional
    public Page<ReviewResponseDTO> showReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByRestaurantforPage(restaurantId, pageable);
        return page.map(ReviewResponseDTO::new);
    }

    @Transactional
    public ReviewResponseDTO update(Long id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        Restaurant restaurant = review.getRestaurant();
        updateRestaurantScoresForReviewUpdate(restaurant, review, requestDTO);
        review.updateReview(requestDTO.getCost(), requestDTO.getPark(), requestDTO.getMood(), requestDTO.getKind(), requestDTO.getTaste(), requestDTO.getStars());
        restaurant.caculateDegree(review.getStars());
        return new ReviewResponseDTO(review);
    }

    private void updateRestaurantScoresForReviewUpdate(Restaurant restaurant, Review oldReview, ReviewRequestDTO newReview) {
        if (oldReview.getTaste() != newReview.getTaste()) {
            restaurant.setTotalTaste(restaurant.getTotalTaste() + (newReview.getTaste() - oldReview.getTaste()));
        }
        if (oldReview.getCost() != newReview.getCost()) {
            restaurant.setTotalCost(restaurant.getTotalCost() + (newReview.getCost() - oldReview.getCost()));
        }
        if (oldReview.getMood() != newReview.getMood()) {
            restaurant.setTotalMood(restaurant.getTotalMood() + (newReview.getMood() - oldReview.getMood()));
        }
        if (oldReview.getKind() != newReview.getKind()) {
            restaurant.setTotalKind(restaurant.getTotalKind() + (newReview.getKind() - oldReview.getKind()));
        }
        if (oldReview.getPark() != newReview.getPark()) {
            restaurant.setTotalPark(restaurant.getTotalPark() + (newReview.getPark() - oldReview.getPark()));
        }
    }

    @Transactional
    public MsgResponseDTO delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        Long restaurantId = review.getRestaurant().getId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(RuntimeException::new);
        restaurant.setTotalReviews(restaurant.getTotalReviews() - 1);
        restaurant.caculateDegree(-review.getStars());
        updateRestaurantScores(restaurant, review, false);
        reviewRepository.delete(review);
        return new MsgResponseDTO("리뷰 삭제 완료", 200);
    }

    @Transactional
    public Page<ReviewResponseDTO> findByAdddress(String word, Pageable pageable) {
        Page<Review> page = reviewRepository.findByAddress(word, pageable);
        return page.map(ReviewResponseDTO::new);
    }
}
