package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {
}
