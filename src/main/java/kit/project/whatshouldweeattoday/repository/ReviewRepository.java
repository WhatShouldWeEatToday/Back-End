package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
