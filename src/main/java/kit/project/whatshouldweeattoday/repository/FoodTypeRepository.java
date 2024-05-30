package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.FoodType;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {
    Optional<FoodType> findByFoodTypeName(String foodTypeName);

    //주간순위
    @Query("SELECT f FROM FoodType f ORDER BY f.count DESC")
    List<FoodType> findTop5ByCount();
}
