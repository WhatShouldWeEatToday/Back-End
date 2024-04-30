package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.likes.LikesRequestDTO;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import kit.project.whatshouldweeattoday.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;
    private final ReviewRepository reviewRepository;

    //리뷰 좋아요 등록
    @PostMapping("/api/review/{reviewId}/likes")
    public Long reviewLikes(@PathVariable Long reviewId) {
        LikesRequestDTO likesRequestDTO = new LikesRequestDTO();
        likesService.save(reviewId, likesRequestDTO);
        return reviewId;
    }

    //리뷰 좋아요 취소
    @DeleteMapping("/api/review/{reviewId}/likes/{likesId}")
    public Long reviewDelete(@PathVariable Long reviewId, @PathVariable Long likesId) {
        likesService.delete(reviewId, likesId);
        return reviewId;
    }
}
