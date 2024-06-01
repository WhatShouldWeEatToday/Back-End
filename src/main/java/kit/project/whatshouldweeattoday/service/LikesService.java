package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.bookmark.BookmarkRequestDTO;
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
    private final LikesRepository likesRepository;
    private final ReviewRepository reviewRepository;
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    //리뷰 좋아요
    //리뷰 좋아요 등록
    @Transactional
    public void save(Long reviewId, LikesRequestDTO likesRequestDTO) {
        String loginId = SecurityUtil.getLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        // 리뷰 조회
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        // 좋아요 상태 확인
        boolean isLiked = likesRepository.existsByReviewIdAndMemberIdAndState(reviewId,member.getId(), true);

        if (isLiked) {
            // 이미 좋아요를 눌렀으면 좋아요 취소
            delete(reviewId);
        } else {
            // 아직 좋아요를 누르지 않았으면 좋아요 등록
            Likes likes = likesRequestDTO.toSaveEntity(member, review, true);
            likesRepository.save(likes);
            review.setTotalLikes(review.getTotalLikes() + 1); // 리뷰 좋아요 개수 증가
        }
    }

    // 리뷰 좋아요 취소
    @Transactional
    public void delete(Long reviewId) {
        String loginId = SecurityUtil.getLoginId();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다."));

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
        Long memberId = member.getId();
        Likes likes = likesRepository.findByReviewIdAndMemberIdAndState(reviewId, memberId, true).orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));
        likesRepository.delete(likes);
        review.setTotalLikes(review.getTotalLikes() - 1); // 리뷰 좋아요 개수 감소
    }

}
