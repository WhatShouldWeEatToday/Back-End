package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.likes.LikesRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.LikesRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {
    @Autowired
    private LikesRepository likesRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    //리뷰 좋아요
    @Transactional
    public void save(Long reviewId, LikesRequestDTO likesRequestDTO){
       Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
               new IllegalArgumentException("공감 실패: 해당 리뷰가 존재하지 않습니다." + reviewId));
        Likes likes = likesRequestDTO.toSaveEntity();
        likes.setReview(review);
        likes.setState(true);
        review.setTotalLikes(review.getTotalLikes()+1);
        likesRepository.save(likes);
    }

    //리뷰 좋아요 취소
    @Transactional
    public void delete(Long reviewId, Long likesId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(() ->
                new IllegalArgumentException("공감 실패: 해당 리뷰가 존재하지 않습니다." + reviewId));
       ;review.setTotalLikes(review.getTotalLikes()-1);
        likesRepository.deleteById(likesId);

    }
}
