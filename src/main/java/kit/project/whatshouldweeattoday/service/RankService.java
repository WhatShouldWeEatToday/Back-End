package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.food.FoodResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.rank.WeeklyFoodTypeRankResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.*;
import kit.project.whatshouldweeattoday.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
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
    private final FoodTypeRepository foodTypeRepository;

    //주간 음식종류 순위
    @Transactional
    public WeeklyFoodTypeRankResponseDTO getTop5RestaurantsByCount() {
        List<Restaurant> topRestaurants = restaurantRepository.findTop5ByCount();
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        List<RestaurantResponseDTO> restaurantResponseDTOs = IntStream.range(0, topRestaurants.size())
                .mapToObj(i -> {
                    Restaurant restaurant = topRestaurants.get(i);
                    RestaurantResponseDTO dto = convertToRestaurantDTO(restaurant, i + 1);
                    return dto;
                })
                .collect(Collectors.toList());

        WeeklyFoodTypeRank weeklyFoodTypeRank = new WeeklyFoodTypeRank();
        weeklyFoodTypeRank.setDate(currentDate);
        weeklyFoodTypeRank.setRestaurant(restaurantResponseDTOs);
        weeklyFoodTypeRankRepository.save(weeklyFoodTypeRank);

        return new WeeklyFoodTypeRankResponseDTO(currentDate, restaurantResponseDTOs);
    }

    @Transactional
    public void resetRestaurantCounts() {
        List<Restaurant> allRestaurants = restaurantRepository.findAll();
        for (Restaurant restaurant : allRestaurants) {
            restaurant.setCount(0L); // count 필드를 0으로 초기화
        }
        restaurantRepository.saveAll(allRestaurants);
    }

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 0시에 실행
    @Transactional
    public void updateWeeklyRankings() {
        // 1. 주간 순위 업데이트
        getTop5RestaurantsByCount();
        // 2. restaurant count 초기화
        resetRestaurantCounts();
    }

    @Transactional
    public RestaurantResponseDTO convertToRestaurantDTO(Restaurant restaurant, int rank) {
        return new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getName(),
                Math.toIntExact(restaurant.getCount()),
                restaurant.getFoodType().getFoodTypeName(),
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

    // food -> foodResponse
    @Transactional
    public FoodResponseDTO convertToFoodDto(Food food, Long rank) {
        return new FoodResponseDTO(
                food.getId(),
                food.getFoodName(),
                food.getCount(),
                rank
        );
    }

    //FoodType init
    public void initData() {
            initFoodTypeData("한식", Arrays.asList("닭요리", "추어", "냉면", "한식뷔페", "기사식당", "국밥", "한식", "한정식", "두부전문점", "해장국", "불고기,두루치기", "호프,요리주점", "철판요리", "설렁탕", "실내포장마차", "쌈밥", "술집", "퓨전한식", "뷔페"));
            initFoodTypeData("중식", Arrays.asList("중국요리", "중식", "양꼬치"));
            initFoodTypeData("일식", Arrays.asList("일식", "초밥,롤", "돈까스,우동", "오뎅바", "일식집", "일본식주점", "일본식라면"));
            initFoodTypeData("양식", Arrays.asList("패밀리레스토랑", "이탈리안", "양식", "스테이크,립"));
            initFoodTypeData("분식", Arrays.asList("분식", "순대", "떡볶이"));
            initFoodTypeData("카페,디저트", Arrays.asList("커피전문점", "아이스크림", "카페", "제과,베이커리", "도넛", "떡,한과", "고양이카페", "샌드위치", "디저트카페", "무인카페", "갤러리카페", "테마카페", "애견카페", "토스트", "간식", "다방", "전통찻집"));
            initFoodTypeData("패스트푸드", Arrays.asList("패스트푸드", "햄버거"));
            initFoodTypeData("아시안", Arrays.asList("인도음식", "베트남음식"));
            initFoodTypeData("피자", Arrays.asList("피자"));
            initFoodTypeData("치킨,통닭,튀김", Arrays.asList("치킨", "닭강정"));
            initFoodTypeData("찜,탕,찌개,전골", Arrays.asList("찌개,전골", "감자탕", "사철탕,영양탕", "샤브샤브", "삼계탕", "매운탕,해물탕"));
            initFoodTypeData("고기,구이", Arrays.asList("육류,고기", "닭요리", "삼겹살", "곱창,막창", "족발,보쌈", "갈비", "오리", "고기뷔페"));
            initFoodTypeData("생선,회,해산물", Arrays.asList("회", "게,대게", "해물,생선", "장어", "복어", "참치회", "조개", "아구", "굴,전복"));
            initFoodTypeData("면,국수", Arrays.asList("냉면", "칼국수", "국수"));
            initFoodTypeData("샐러드,포케", Arrays.asList("샐러드"));
            initFoodTypeData("도시락", Arrays.asList("도시락", "주먹밥", "배달도시락"));
            initFoodTypeData("죽,백반", Arrays.asList("죽"));

    }

    private void initFoodTypeData(String foodTypeName, List<String> restaurantTypes) {
        FoodType foodType = foodTypeRepository.findByFoodTypeName(foodTypeName).orElseThrow(() -> new IllegalArgumentException("FoodType " + foodTypeName + " not found"));
        List<Restaurant> restaurants = restaurantRepository.findByRestaurantTypeIn(restaurantTypes);
        restaurants.forEach(restaurant -> {
            restaurant.setFoodType(foodType);
            restaurantRepository.save(restaurant);
        });
    }
}
