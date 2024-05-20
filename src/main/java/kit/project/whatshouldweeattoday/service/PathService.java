package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {
    private final RestaurantRepository restaurantRepository;
    private final TMapService tmapService;
    private final ReviewService reviewService;
    private int serialNum = 0;
    private List<PersonalPath> weightArray = new ArrayList<>(); //전체 가중치

    // 사용자별 음식점 경로 배열
    @Transactional
    public List<Restaurant> pathList(double startX, double startY, String searchDttm) {
        String userAddress = tmapService.getAddressByCoordinates2(startX, startY);
        List<Restaurant> restaurants = restaurantRepository.findByOnlyAddress(userAddress);
        restaurants = sortByDegree(restaurants);
        restaurants = getPath(startX, startY, restaurants, searchDttm, 0);
        restaurants = sortByPath(restaurants);
        return restaurants;
    }

    // 리뷰 평점순으로 정렬
    @Transactional
    public List<Restaurant> sortByDegree(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getDegree(), r1.getDegree()))
                .limit(20)
                .collect(Collectors.toList());
    }

    // 경로 시간 구하기
    @Transactional
    public List<Restaurant> getPath(double startX, double startY, List<Restaurant> restaurants, String searchDttm, int start) {
        for (int i = start; i < restaurants.size(); i++) {
            restaurants.get(i).setPathTime(tmapService.totalTime(
                    Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurants.get(i).getLongitude()), Double.toString(restaurants.get(i).getLatitude()),
                    0, "json", 10, searchDttm));
        }
        return restaurants;
    }

    // 거리 기준으로 정렬
    @Transactional
    public List<Restaurant> sortByPath(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted(Comparator.comparingInt(Restaurant::getPathTime))
                .limit(5)
                .collect(Collectors.toList());
    }

    // 사용자별 가중치 정보 수집
    @Transactional
    public void getWeightInfo(List<Double> startXs, List<Double> startYs, String searchDttm) {
        List<List<PersonalPath>> allPersonalPaths = new ArrayList<>();

        // 각 사용자별로 경로와 가중치 배열 생성
        for (int i = 0; i < startXs.size(); i++) {
            double startX = startXs.get(i);
            double startY = startYs.get(i);

            // 각 사용자의 위치 정보를 기반으로 음식점 목록을 수집합니다.
            List<Restaurant> userRestaurants = pathList(startX, startY, searchDttm);

            // 사용자별로 수집한 음식점 목록을 통합할 빈 리스트를 생성합니다.
            List<Restaurant> mergedList = mergeList(userRestaurants, new ArrayList<>());

            // 통합된 음식점 목록에 대해 경로 시간을 계산합니다.
            mergedList = getPath(startX, startY, mergedList, searchDttm, 0);

            // 경로 시간을 기준으로 음식점 목록을 정렬합니다.
            mergedList = sortByPath(mergedList);

            // 개인별 경로 목록을 생성합니다.
            List<PersonalPath> personalPaths = getPersonal(mergedList, 0);

            // 인덱스를 기반으로 가중치를 부여합니다.
            applyIndexBasedWeights(personalPaths);

            // 모든 사용자별 경로 목록을 저장합니다.
            allPersonalPaths.add(personalPaths);
        }

        // 최종 가중치를 계산합니다.
        calculateFinalWeights(allPersonalPaths);
    }

    // 가중치 배열 만들기 (모든 개인별 배열 넣어줌)
    public List<PersonalPath> getPersonal(List<Restaurant> restaurants, int start) {
        List<PersonalPath> personalPaths = new ArrayList<>();
        for (int i = start; i < restaurants.size(); i++) {
            PersonalPath personalPath = new PersonalPath(restaurants.get(i), restaurants.get(i).getPathTime(), serialNum);
            personalPaths.add(personalPath);
            serialNum++;
        }
        return personalPaths;
    }

    // 가중치 부여
    private void applyIndexBasedWeights(List<PersonalPath> personalPaths) {
        for (int i = 0; i < personalPaths.size(); i++) {
            PersonalPath personalPath = personalPaths.get(i);
            personalPath.setWeight(i + 1); // 인덱스 값을 가중치로 사용
        }
    }

    // 최종 가중치 계산
    private void calculateFinalWeights(List<List<PersonalPath>> allPersonalPaths) {
        Map<Long, Integer> weightMap = new HashMap<>();

        // 각 사용자별 가중치 합산
        for (List<PersonalPath> personalPaths : allPersonalPaths) {
            for (PersonalPath path : personalPaths) {
                weightMap.put(path.getRestaurant().getId(),
                        weightMap.getOrDefault(path.getRestaurant().getId(), 0) + path.getWeight());
            }
        }

        // 결과 출력 (또는 원하는 방식으로 반환)
        weightMap.forEach((id, weight) -> {
            System.out.println("Restaurant ID: " + id + ", Total Weight: " + weight);
        });
    }

    // 상대방 배열과 내 배열 합침
    @Transactional
    public List<Restaurant> mergeList(List<Restaurant> restaurants1, List<Restaurant> restaurants2) {
        List<Restaurant> mergedList = new ArrayList<>(restaurants1);
        mergedList.addAll(restaurants2);
        return mergedList;
    }
}
