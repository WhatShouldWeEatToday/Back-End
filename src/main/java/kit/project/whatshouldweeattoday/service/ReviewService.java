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

    //리뷰 등록 -> 음식점 total 점수도 바뀌어야함
    @Transactional
    public void save(Long id,ReviewRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        restaurant.setTotalReviews(restaurant.getTotalReviews()+1);
        Review review = new Review(requestDTO);
        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        review.setWriter(member.getNickname());
        review.confirmMember(member);
        review.setRestaurant(restaurant);
        reviewRepository.save(review);
        if(review.getMood()==1){
            restaurant.setTotalMood(restaurant.getTotalMood()+1);
        }
        if(review.getKind()==1){
            restaurant.setTotalKind(restaurant.getTotalKind()+1);
        }
        if(review.getCost()==1){
            restaurant.setTotalCost(restaurant.getTotalCost()+1);
        }
        if(review.getPark()==1){
            restaurant.setTotalPark(restaurant.getTotalPark()+1);
        }
        if(review.getTaste()==1){
            restaurant.setTotalTaste(restaurant.getTotalTaste()+1);
        }
        restaurant.caculateDegree(review.getStars());
    }

    //리뷰 전체조회
    @Transactional
    public Page<RestaurantResponseDTO> findAll(String address, Pageable pageable) {
        Page<Restaurant> pages;
        System.out.println(" 주소 : "+address);
        if (address == null || address.trim().isEmpty()) {
            // address가 null이거나 빈 문자열인 경우, 최신순 리뷰가 등록된걸로 조회
            pages=restaurantRepository.findAllByReviewCreated(pageable);
        } else {
            //  address가 있는 경우, 해당 address를 포함하는 음식점과 리뷰 조회
            pages = restaurantRepository.findByAddress(address, pageable);
        }

        Page<RestaurantResponseDTO> dtoPage = pages.map(restaurant -> {
            Page<ReviewResponseDTO> reviewList = this.showReviewsByRestaurant(restaurant.getId(), Pageable.ofSize(5));
         //Long id, String name, String restaurantType, Double degree, String addressRoad, String addressNumber, String tel, String menus, int totalReviews, int totalTaste, int totalCost, int totalKind, int totalMood, int totalPark, Page<ReviewResponseDTO> reviewList
            RestaurantResponseDTO responseDTO = new RestaurantResponseDTO(
                    restaurant.getId(),
                    restaurant.getName(),
                    restaurant.getRestaurantType(),
                    restaurant.getDegree(),
                    restaurant.getAddressRoad(),
                    restaurant.getAddressNumber(),
                    restaurant.getTel(),
                    restaurant.getMenus(),
                    restaurant.getTotalReviews(),
                    restaurant.getTotalTaste(),
                    restaurant.getTotalCost(),
                    restaurant.getTotalKind(),
                    restaurant.getTotalMood(),
                    restaurant.getTotalPark(),
                    reviewList
            );
            return responseDTO;
        });
        return dtoPage;
    }

    //리뷰 상세화면 및 수정
    @Transactional
    public ReviewResponseDTO reviewDetails(Long restaurantId,Long id) {
       Review review = reviewRepository.findById(id)
               .orElseThrow(IllegalArgumentException::new);
       return new ReviewResponseDTO(review);
    }

    //한 가게의 리뷰 조회
    @Transactional
    public Page<ReviewResponseDTO> showReviewsByRestaurant(Long restaurantId, Pageable pageable) {
        Page<Review> page = reviewRepository.findByRestaurantforPage(restaurantId, pageable);
        Page<ReviewResponseDTO> dtoPage = page.map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO(review);
            return dto;
        });
        return dtoPage;
    }

    //리뷰 수정
    @Transactional //얘 안붙이면 mysql에 수정데이터 안들어감
    public ReviewResponseDTO update(Long id, ReviewRequestDTO requestDTO) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
        review.updateReview(requestDTO.getCost(),requestDTO.getPark(),requestDTO.getMood(),requestDTO.getKind(),requestDTO.getTaste(),requestDTO.getStars());
        Restaurant restaurant = review.getRestaurant();
        if(review.getMood()==1){
            restaurant.setTotalMood(restaurant.getTotalMood()+1);
        }else if(review.getMood()==0 && restaurant.getTotalMood()>0){
            restaurant.setTotalMood(restaurant.getTotalMood()-1);
        }

        if(review.getKind()==1){
            restaurant.setTotalKind(restaurant.getTotalKind()+1);
        }else if(review.getKind()==0 && restaurant.getTotalKind()>0){
            restaurant.setTotalKind(restaurant.getTotalKind()-1);
        }

        if(review.getCost()==1){
            restaurant.setTotalCost(restaurant.getTotalCost()+1);
        }else if(review.getCost()==0&& restaurant.getTotalCost()>0){
            restaurant.setTotalCost(restaurant.getTotalCost()-1);
        }

        if(review.getPark()==1){
            restaurant.setTotalPark(restaurant.getTotalPark()+1);
        }else if(review.getPark()==0&& restaurant.getTotalPark()>0){
            restaurant.setTotalPark(restaurant.getTotalPark()-1);
        }

        if(review.getTaste()==1){
            restaurant.setTotalTaste(restaurant.getTotalTaste()+1);
        }else if(review.getTaste()==0&& restaurant.getTotalTaste()>0){
            restaurant.setTotalTaste(restaurant.getTotalTaste()-1);
        }
        restaurant.caculateDegree(review.getStars());
        return new ReviewResponseDTO(review);
    }

    //리뷰 삭제
    @Transactional
    public MsgResponseDTO delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        Long restaurantId = review.getRestaurant().getId();

        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(RuntimeException::new);
        restaurant.setTotalReviews(restaurant.getTotalReviews()-1);
        restaurant.setTotalCost(restaurant.getTotalCost()-1);
        restaurant.setTotalMood(restaurant.getTotalMood()-1);
        restaurant.setTotalPark(restaurant.getTotalPark()-1);
        restaurant.setTotalKind(restaurant.getTotalKind()-1);
        restaurant.setTotalTaste(restaurant.getTotalTaste()-1);
        restaurant.caculateDegree(-review.getStars());

        if(review.getMood()==1){
            restaurant.setTotalMood(restaurant.getTotalMood()-1);
        }
        if(review.getKind()==1){
            restaurant.setTotalKind(restaurant.getTotalKind()-1);
        }
        if(review.getCost()==1){
            restaurant.setTotalCost(restaurant.getTotalCost()-1);
        }
        if(review.getPark()==1){
            restaurant.setTotalPark(restaurant.getTotalPark()-1);
        }
        if(review.getTaste()==1){
            restaurant.setTotalTaste(restaurant.getTotalTaste()-1);
        }
        reviewRepository.delete(review);
        return new MsgResponseDTO("리뷰 삭제 완료", 200);
    }
    @Transactional
    public Page<ReviewResponseDTO> findByAdddress(String word, Pageable pageable) {
        Page<Review> page = reviewRepository.findByAddress(word, pageable);

        Page<ReviewResponseDTO> dtoPage = page.map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO(review);
            return dto;
        });
        return dtoPage;
    }
}
