package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Likes;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Likes l WHERE l.review.id = :reviewId")
    void deleteByReviewId(@Param("reviewId") Long reviewId);
    boolean existsByReviewIdAndMemberIdAndState(Long reviewId, Long memberId, boolean state);
    Optional<Likes> findByReviewIdAndMemberIdAndState(Long reviewId, Long memberId, boolean state);

}
