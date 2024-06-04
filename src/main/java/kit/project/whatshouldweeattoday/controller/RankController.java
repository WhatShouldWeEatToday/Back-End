package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodTypeRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.FoodType;
import kit.project.whatshouldweeattoday.repository.FoodRepository;
import kit.project.whatshouldweeattoday.repository.FoodTypeRepository;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.service.RankService;
import kit.project.whatshouldweeattoday.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rank")
public class RankController {

    private final RestaurantService restaurantService;
    private final RankService rankService;

    // -> 순위초기화
    @GetMapping("/update-weekly")
    public String updateWeeklyRankings() {
        rankService.updateWeeklyRankings();
        return "Weekly rankings updated and counts reset";
    }


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

    //setFoodType
    @PatchMapping("/init")
    public void initData() {
        rankService.initData();
    }

    //restaurant의 foodType count 초기화
    @PatchMapping("/initCountFoodType")
    public void initCount() {
        rankService.initCount();
    }

    //food count 초기화
    @PatchMapping("/initCountFood")
    public void initFoodCount() {
        rankService.initFoodCount();
    }

}
