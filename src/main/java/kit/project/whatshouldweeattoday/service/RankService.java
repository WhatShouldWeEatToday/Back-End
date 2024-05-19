package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodTypeRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.WeeklyFoodTypeRank;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
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


    @Transactional
    public WeeklyFoodTypeRankResponseDTO getTop5RestaurantsByCount() {
        List<Restaurant> topRestaurants = restaurantRepository.findTop5ByCount();
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        WeeklyFoodTypeRank weeklyFoodTypeRank = new WeeklyFoodTypeRank();
        weeklyFoodTypeRank.setDate(currentDate);

        List<RestaurantResponseDTO> restaurantResponseDTOs = IntStream.range(0, topRestaurants.size())
                .mapToObj(i -> {
                    Restaurant restaurant = topRestaurants.get(i);
                    RestaurantResponseDTO dto = convertToDto(restaurant, i + 1);
                    return dto;
                })
                .collect(Collectors.toList());

        weeklyFoodTypeRank.setFoods(restaurantResponseDTOs);
        weeklyFoodTypeRankRepository.save(weeklyFoodTypeRank);

        return new WeeklyFoodTypeRankResponseDTO(currentDate, restaurantResponseDTOs);
    }

    @Transactional
    public RestaurantResponseDTO convertToDto(Restaurant restaurant, int rank) {
        return new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCount(),
                restaurant.getRestaurantType(),
                rank
        );
    }
}
