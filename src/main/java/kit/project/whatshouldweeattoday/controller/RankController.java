package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodTypeRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.service.RankService;
import kit.project.whatshouldweeattoday.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final RestaurantService restaurantService;
    private final RankService rankService;

    //주간 음식종류별 순위
    @GetMapping("/foodType")
    public ResponseEntity<WeeklyFoodTypeRankResponseDTO> getTypeRank() {
        WeeklyFoodTypeRankResponseDTO topRestaurants = rankService.getTop5RestaurantsByCount();
        return ResponseEntity.ok(topRestaurants);
    }
    
    //주간 음식별 순위 -> 채팅방 투표결과 기준
    @GetMapping("food")
    public ResponseEntity<WeeklyFoodRankResponseDTO> getFoodRank() {
        WeeklyFoodRankResponseDTO topFoods = rankService.getTop5FoodsByChat();
       return ResponseEntity.ok(topFoods);
    }

    // 강제로 @Scheduled 메서드를 호출하는 테스트용 엔드포인트
    @GetMapping("/foodType/testScheduled")
    public ResponseEntity<Void> testScheduled() {
        rankService.updateWeeklyRankings();
        return ResponseEntity.ok().build();
    }
}
