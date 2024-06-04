package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    // 리뷰 좋아요 등록 또는 취소
    @PostMapping("/api/review/{reviewId}/likes")
    public ResponseEntity<?> reviewLikes(@PathVariable Long reviewId) {
        try {
            // 좋아요 등록 또는 취소
            likesService.toggleLike(reviewId);
            return ResponseEntity.ok().build(); // 성공 응답
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to toggle review likes"); // 실패 응답
        }
    }
}
