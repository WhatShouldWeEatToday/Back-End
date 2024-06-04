package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.FoodType;
import kit.project.whatshouldweeattoday.domain.entity.Member;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.domain.type.ReviewType;
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
    public void updateRestaurantScores(Restaurant restaurant, Review review, boolean isAdding) {
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

    //리뷰날짜형식수정
    @Transactional
    public void updateDateFormat() {
        List<Review> reviewList = reviewRepository.findAll();

        for (Review review : reviewList) {
            review.updateCreatedDateFormat();
        }
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

    //멤버한명이 리뷰 n개 작성
    public void saveData() {
        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        Restaurant restaurant1 = restaurantRepository.findById(26L)
                .orElseThrow(IllegalArgumentException::new);
        FoodType foodType1 = foodTypeRepository.findById(restaurant1.getFoodType().getId()).orElseThrow(IllegalArgumentException::new);
        restaurant1.setTotalReviews(restaurant1.getTotalReviews() + 1);
        restaurant1.setCount(restaurant1.getCount() + 1);
        foodType1.setCount(foodType1.getCount()+1);

        Review review1 = Review.builder()
                .taste(0)
                .cost(0)
                .mood(0)
                .kind(0)
                .park(0)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(3.0)
                .totalLikes(0L)
                .build();

        review1.setWriter(member.getNickname());
        review1.confirmMember(member);
        review1.setRestaurant(restaurant1);
        reviewRepository.save(review1);
        updateRestaurantScores(restaurant1, review1, true);
        restaurant1.calculateDegree(review1.getStars());

        Restaurant restaurant2 = restaurantRepository.findById(30L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant2.setTotalReviews(restaurant2.getTotalReviews() + 1);
        restaurant2.setCount(restaurant2.getCount() + 1);
        foodType1.setCount(foodType1.getCount()+1);
        Review review2 = Review.builder()
                .taste(1)
                .cost(1)
                .mood(1)
                .kind(0)
                .park(0)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(4.0)
                .totalLikes(0L)
                .build();

        review2.setWriter(member.getNickname());
        review2.confirmMember(member);
        review2.setRestaurant(restaurant2);
        reviewRepository.save(review2);
        updateRestaurantScores(restaurant2, review2, true);
        restaurant2.calculateDegree(review2.getStars());

        Restaurant restaurant3 = restaurantRepository.findById(42L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant3.setTotalReviews(restaurant3.getTotalReviews() + 1);
        restaurant3.setCount(restaurant3.getCount() + 1);
        foodType1.setCount(foodType1.getCount()+1);

        Review review3 = Review.builder()
                .taste(0)
                .cost(0)
                .mood(0)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(4.5)
                .totalLikes(0L)
                .build();

        review3.setWriter(member.getNickname());
        review3.confirmMember(member);
        review3.setRestaurant(restaurant3);
        reviewRepository.save(review3);
        updateRestaurantScores(restaurant3, review3, true);
        restaurant3.calculateDegree(review3.getStars());

        Restaurant restaurant4 = restaurantRepository.findById(52L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant4.setTotalReviews(restaurant4.getTotalReviews() + 1);
        restaurant4.setCount(restaurant4.getCount() + 1);
        foodType1.setCount(foodType1.getCount()+1);

        Review review4 = Review.builder()
                .taste(1)
                .cost(1)
                .mood(1)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(5.0)
                .totalLikes(0L)
                .build();

        review4.setWriter(member.getNickname());
        review4.confirmMember(member);
        review4.setRestaurant(restaurant4);
        reviewRepository.save(review4);
        updateRestaurantScores(restaurant4, review4, true);
        restaurant4.calculateDegree(review4.getStars());

        //foodType이 6인것
        Restaurant restaurant5 = restaurantRepository.findById(50L)
                .orElseThrow(IllegalArgumentException::new);
        FoodType foodType2 = foodTypeRepository.findById(restaurant5.getFoodType().getId()).orElseThrow(IllegalArgumentException::new);
        restaurant5.setTotalReviews(restaurant5.getTotalReviews() + 1);
        restaurant5.setCount(restaurant5.getCount() + 1);
        foodType2.setCount(foodType2.getCount()+1);

        Review review5 = Review.builder()
                .taste(1)
                .cost(1)
                .mood(0)
                .kind(1)
                .park(0)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(2.0)
                .totalLikes(0L)
                .build();

        review5.setWriter(member.getNickname());
        review5.confirmMember(member);
        review5.setRestaurant(restaurant5);
        reviewRepository.save(review5);
        updateRestaurantScores(restaurant5, review5, true);
        restaurant5.calculateDegree(review5.getStars());

        Restaurant restaurant6 = restaurantRepository.findById(51L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant6.setTotalReviews(restaurant6.getTotalReviews() + 1);
        restaurant6.setCount(restaurant6.getCount() + 1);
        foodType2.setCount(foodType2.getCount()+1);

        Review review6 = Review.builder()
                .taste(1)
                .cost(0)
                .mood(0)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(3.7)
                .totalLikes(0L)
                .build();

        review6.setWriter(member.getNickname());
        review6.confirmMember(member);
        review6.setRestaurant(restaurant6);
        reviewRepository.save(review6);
        updateRestaurantScores(restaurant6, review6, true);
        restaurant6.calculateDegree(review6.getStars());

        Restaurant restaurant7 = restaurantRepository.findById(53L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant7.setTotalReviews(restaurant7.getTotalReviews() + 1);
        restaurant7.setCount(restaurant7.getCount() + 1);
        foodType2.setCount(foodType2.getCount()+1);

        Review review7 = Review.builder()
                .taste(0)
                .cost(0)
                .mood(0)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(2.7)
                .totalLikes(0L)
                .build();

        review7.setWriter(member.getNickname());
        review7.confirmMember(member);
        review7.setRestaurant(restaurant7);
        reviewRepository.save(review7);
        updateRestaurantScores(restaurant7, review7, true);
        restaurant7.calculateDegree(review7.getStars());


        //foodType이 5인것
        Restaurant restaurant8 = restaurantRepository.findById(61L)
                .orElseThrow(IllegalArgumentException::new);
        FoodType foodType3 = foodTypeRepository.findById(restaurant8.getFoodType().getId()).orElseThrow(IllegalArgumentException::new);
        restaurant8.setTotalReviews(restaurant8.getTotalReviews() + 1);
        restaurant8.setCount(restaurant8.getCount() + 1);
        foodType3.setCount(foodType3.getCount()+1);

        Review review8 = Review.builder()
                .taste(0)
                .cost(1)
                .mood(1)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(4.7)
                .totalLikes(0L)
                .build();

        review8.setWriter(member.getNickname());
        review8.confirmMember(member);
        review8.setRestaurant(restaurant8);
        reviewRepository.save(review8);
        updateRestaurantScores(restaurant8, review8, true);
        restaurant8.calculateDegree(review8.getStars());

        Restaurant restaurant9 = restaurantRepository.findById(68L)
                .orElseThrow(IllegalArgumentException::new);
        restaurant9.setTotalReviews(restaurant9.getTotalReviews() + 1);
        restaurant9.setCount(restaurant9.getCount() + 1);
        foodType3.setCount(foodType3.getCount()+1);

        Review review9 = Review.builder()
                .taste(1)
                .cost(1)
                .mood(1)
                .kind(1)
                .park(1)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(4.7)
                .totalLikes(0L)
                .build();

        review9.setWriter(member.getNickname());
        review9.confirmMember(member);
        review9.setRestaurant(restaurant9);
        reviewRepository.save(review9);
        updateRestaurantScores(restaurant9, review9, true);
        restaurant9.calculateDegree(review9.getStars());


        //footType이 14인것
        Restaurant restaurant10 = restaurantRepository.findById(66L)
                .orElseThrow(IllegalArgumentException::new);
        FoodType foodType4 = foodTypeRepository.findById(restaurant10.getFoodType().getId()).orElseThrow(IllegalArgumentException::new);
        restaurant10.setTotalReviews(restaurant10.getTotalReviews() + 1);
        restaurant10.setCount(restaurant10.getCount() + 1);
        foodType4.setCount(foodType4.getCount()+1);

        Review review10 = Review.builder()
                .taste(0)
                .cost(1)
                .mood(0)
                .kind(1)
                .park(0)
                .reviewType(ReviewType.NOT_CERTIFY)
                .stars(2.9)
                .totalLikes(0L)
                .build();

        review10.setWriter(member.getNickname());
        review10.confirmMember(member);
        review10.setRestaurant(restaurant10);
        reviewRepository.save(review10);
        updateRestaurantScores(restaurant10, review10, true);
        restaurant10.calculateDegree(review10.getStars());
    }
}
