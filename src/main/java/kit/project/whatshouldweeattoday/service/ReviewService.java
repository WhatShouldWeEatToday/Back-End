package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.FoodType;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.FoodTypeRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodTypeRepository foodTypeRepository;

    //리뷰등록
    @Transactional
    public void save(Long id, ReviewRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        FoodType foodType = foodTypeRepository.findById(restaurant.getFoodType().getId()).orElseThrow(IllegalArgumentException::new);
        restaurant.setTotalReviews(restaurant.getTotalReviews() + 1);
        restaurant.setCount(restaurant.getCount() + 1);
        foodType.setCount(foodType.getCount()+1);
        Review review = new Review(requestDTO);
        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        review.setWriter(member.getNickname());
        review.confirmMember(member);
        review.setRestaurant(restaurant);
        review.setStars(requestDTO.getStars());
        reviewRepository.save(review);
        updateRestaurantScores(restaurant, review, true);
        restaurant.calculateDegree(review.getStars());
    }

    //Restaurant 개인별 점수 등록
    private void updateRestaurantScores(Restaurant restaurant, Review review, boolean isAdding) {
        int delta = isAdding ? 1 : -1;
        restaurant.calculateDegree(review.getStars());//-> 리뷰평점
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
                ? restaurantRepository.findAll(pageable)
                : restaurantRepository.findByAddress(address, pageable);

        return pages.map(restaurant -> {
            Page<ReviewResponseDTO> reviewList = this.showReviewsByRestaurant(restaurant.getId(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
            return new RestaurantResponseDTO(restaurant, reviewList);
        });
    }

    @Transactional
    public ReviewResponseDTO reviewDetails(Long restaurantId, Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        return new ReviewResponseDTO(review);
    }


    //리뷰수정
    @Transactional
    public ReviewResponseDTO update(Long id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        Restaurant restaurant = review.getRestaurant();
        updateRestaurantScoresForReviewUpdate(restaurant, review, requestDTO);
        review.updateReview(requestDTO.getCost(), requestDTO.getPark(), requestDTO.getMood(), requestDTO.getKind(), requestDTO.getTaste(), requestDTO.getStars());
        restaurant.calculateDegree(review.getStars());
        return new ReviewResponseDTO(review);
    }

    //리뷰수정 로직
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
        restaurant.calculateDegree(-review.getStars());
        updateRestaurantScores(restaurant, review, false);
        reviewRepository.delete(review);
        return new MsgResponseDTO("리뷰 삭제 완료", 200);
    }

    //읍면동 조회 -> 리뷰만 출력
    @Transactional
    public Page<ReviewResponseDTO> findByAdddress(String word, Pageable pageable) {
        Page<Review> page = reviewRepository.findByAddress(word, pageable);
        return page.map(ReviewResponseDTO::new);
    }

    //읍면동 조회 -> 리뷰 및 음식점 출력
    public Page<RestaurantResponseDTO> getAllByAddress(String address, Pageable pageable) {
        // 읍,면,동으로 조회한 음식점
        List<Restaurant> restaurants = restaurantRepository.findByAddressLatestReview(address);

        // 변환 작업 및 최신 리뷰의 createdDate 기준 정렬
        List<RestaurantResponseDTO> responseDTOs = restaurants.stream().map(restaurant -> {
                    // 음식점 안에 review들을 createdDate순으로 정렬(한 페이지당 5개)
                    Page<ReviewResponseDTO> reviewList = this.showReviewsByRestaurant(restaurant.getId(), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDate")));
                    // RestaurantResponseDTO 객체 생성
                    return new RestaurantResponseDTO(restaurant, reviewList);
                }).sorted(Comparator.comparing(dto -> dto.getReviewList().getContent().isEmpty() ? null : dto.getReviewList().getContent().get(0).getCreatedDate(), Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        // Pageable 객체가 없으면 기본값 설정
        if (pageable == null) {
            System.out.println("Pageable 객체가 없음");
            pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        }

        // Page 객체로 변환
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responseDTOs.size());
        Page<RestaurantResponseDTO> page = new PageImpl<>(responseDTOs.subList(start, end), pageable, responseDTOs.size());

        return page;
    }

    //음식점 아이디로 리뷰 반환
    @Transactional
    public Page<ReviewResponseDTO> showReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByRestaurantforPage(restaurantId, pageable);
        return page.map(ReviewResponseDTO::new);
    }
}
