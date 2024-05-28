package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodService {

    private final FoodRepository foodRepository;

    public String getImageRouteByFoodName(String foodName) throws BadRequestException {
        Food food = foodRepository.findByFoodName(foodName)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        return food.getImageRoute();
    }

    public void increaseFoodCount(String foodName) throws BadRequestException {
        Food food = foodRepository.findByFoodName(foodName)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        food.setCount(food.getCount() + 1);
        foodRepository.save(food);
    }
}
