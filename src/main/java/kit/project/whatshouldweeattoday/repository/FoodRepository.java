package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    @Query("SELECT f FROM Food f ORDER BY f.count DESC")
    List<Food> findTop5ByCount();

    Optional<Food> findByFoodName(String foodName);
}
