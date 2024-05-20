package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.food.FoodResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodTypeRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.WeeklyFoodRank;
import kit.project.whatshouldweeattoday.domain.entity.WeeklyFoodTypeRank;
import kit.project.whatshouldweeattoday.repository.FoodRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.repository.WeeklyFoodRankRepository;
import kit.project.whatshouldweeattoday.repository.WeeklyFoodTypeRankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankService {
    private final RestaurantRepository restaurantRepository;
    private final WeeklyFoodTypeRankRepository weeklyFoodTypeRankRepository;
    private final WeeklyFoodRankRepository weeklyFoodRankRepository;
    private final FoodRepository foodRepository;


    @Transactional
    public WeeklyFoodTypeRankResponseDTO getTop5RestaurantsByCount() {
        List<Restaurant> topRestaurants = restaurantRepository.findTop5ByCount();
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        WeeklyFoodTypeRank weeklyFoodTypeRank = new WeeklyFoodTypeRank();
        weeklyFoodTypeRank.setDate(currentDate);

        List<RestaurantResponseDTO> restaurantResponseDTOs = IntStream.range(0, 5)//topRestaurants.size()
                .mapToObj(i -> {
                    Restaurant restaurant = topRestaurants.get(i);
                    RestaurantResponseDTO dto = convertToRestaurantDTO(restaurant, i + 1);
                    return dto;
                })
                .collect(Collectors.toList());

        weeklyFoodTypeRank.setFoods(restaurantResponseDTOs);
        weeklyFoodTypeRankRepository.save(weeklyFoodTypeRank);

        return new WeeklyFoodTypeRankResponseDTO(currentDate, restaurantResponseDTOs);
    }

    @Transactional
    public RestaurantResponseDTO convertToRestaurantDTO(Restaurant restaurant, int rank) {
        return new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCount(),
                restaurant.getRestaurantType(),
                rank
        );
    }

    @Transactional
    public WeeklyFoodRankResponseDTO getTop5FoodsByChat() {
        List<Food> topFoods = foodRepository.findTop5ByCount();
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        WeeklyFoodRank weeklyFoodRank = new WeeklyFoodRank();
        weeklyFoodRank.setDate(currentDate);

        List<FoodResponseDTO> foodResponseDTOS = IntStream.range(0,5)
                .mapToObj(i ->{
                    Food food = topFoods.get(i);
                    FoodResponseDTO dto = convertToFoodDto(food,i+1L);
                    return dto;
                })
                .collect(Collectors.toList());

        weeklyFoodRank.setFoods(foodResponseDTOS);
        weeklyFoodRankRepository.save(weeklyFoodRank);
        return new WeeklyFoodRankResponseDTO(currentDate, foodResponseDTOS);
    }

    @Transactional
    public FoodResponseDTO convertToFoodDto(Food food, Long rank) {
        return new FoodResponseDTO(
                food.getId(),
                food.getFoodName(),
                food.getCount(),
                rank
        );
    }
}
