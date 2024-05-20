package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.WeeklyFoodRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyFoodRankRepository extends JpaRepository<WeeklyFoodRank, Long> {
}
