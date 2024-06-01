package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.bookmark.BookmarkResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.likes.LikesRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.likes.LikesResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.*;
import kit.project.whatshouldweeattoday.repository.LikesRepository;
import kit.project.whatshouldweeattoday.repository.MemberRepository;
import kit.project.whatshouldweeattoday.repository.NoticeRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import kit.project.whatshouldweeattoday.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LikesService {
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private NoticeRepository noticeRepository;
    @Autowired
    private MemberRepository memberRepository;

    //리뷰 좋아요
    @Transactional
    public LikesResponseDTO save(Long reviewId, LikesRequestDTO likesRequestDTO){
        Member member = getCurrentMember();

       Review review = reviewRepository.findById(reviewId).orElseThrow(RuntimeException::new);
        Likes likes = likesRequestDTO.toSaveEntity();
        likes.setReview(review);
        likes.setState(true);
        likes.setMember(member);
        review.setTotalLikes(review.getTotalLikes()+1); //-> 리뷰 총 좋아요 개수 up
        String noticeContent = likes.getMember() +"님이 "+review.getRestaurant().getName()+"의 리뷰에 좋아요를 눌렀습니다.";
        LocalDateTime localDateTime = LocalDateTime.now();
        Notice notice = new Notice(review.getMember(),noticeContent,localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));// -> 좋아요알림
        likesRepository.save(likes);
        noticeRepository.save(notice);
        return new LikesResponseDTO(likes);
    }

    //리뷰 좋아요 취소
    @Transactional
    public MsgResponseDTO delete(Long reviewId, Long likesId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(RuntimeException::new);
        review.setTotalLikes(review.getTotalLikes()-1);
        likesRepository.deleteById(likesId);
        return new MsgResponseDTO("좋아요 취소", 200);
    }

    private Member getCurrentMember() {
        String loginId = SecurityUtil.getLoginId();
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));
    }
}
