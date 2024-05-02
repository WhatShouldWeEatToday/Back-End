package kit.project.whatshouldweeattoday.service;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;

    //리뷰 등록
    @Transactional
    public void save(Long id,ReviewRequestDTO requestDTO) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);

        Review review = new Review(requestDTO);
        Member member = memberRepository.findByLoginId(SecurityUtil.getLoginId()).orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
        review.setWriter(member.getNickname());
        review.confirmMember(member);
        review.setRestaurant(restaurant);
        reviewRepository.save(review);
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
