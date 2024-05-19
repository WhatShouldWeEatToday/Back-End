package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.WeeklyFoodTypeRank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeeklyFoodTypeRankRepository extends JpaRepository<WeeklyFoodTypeRank, Long> {
    //Optional : 값이 존재할 수도 있고 존재하지 않을 수도 있는 객체를 나타냄, NullPointerException 방지
    Optional<WeeklyFoodTypeRank> findByDate(String date);
}
