package kit.project.whatshouldweeattoday.service;


import kit.project.whatshouldweeattoday.domain.dto.restaurant.PersonalPath;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import kit.project.whatshouldweeattoday.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final TMapService tmapService;
    private final ReviewService reviewService;
    private int serialNum = 0;
    private List<PersonalPath> weithtArray = new ArrayList<>(); //전체 가중치

    //keyword로 맛집을 검색함(메뉴명, 점포명)
    @Transactional
    public Page<RestaurantResponseDTO> searchRestaurants(String keyword, Pageable pageable) {
        System.out.println("searchRestaurants 함수 : " + keyword);
        Page<Restaurant> restaurantPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            restaurantPage = restaurantRepository.findAll(pageable);
        } else {
            restaurantPage = restaurantRepository.findByMenuContainingOrNameContaining(keyword, pageable);
        }
        // JPA Repository에서 Page<Restaurant>를 반환받음
        Page<RestaurantResponseDTO> dtoPage = restaurantPage.map(restaurant -> {
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            return dto;
        });
        return dtoPage;
    }

    //카페만 반환
    @Transactional
    public Page<RestaurantResponseDTO> searchOnlyCafes(String keyword, Pageable pageable) {
        System.out.println("searchOnlyCafes 함수 : " + keyword);
        Page<Restaurant> cafes;

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 문자열인 경우, 모든 카페를 검색
            cafes = restaurantRepository.findAllCafes(pageable);
        } else {
            // keyword가 있는 경우, 해당 키워드를 포함하는 카페만 검색
            cafes = restaurantRepository.findOnlyCafes(keyword, pageable);
        }

        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = cafes.map(restaurant -> {
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            return dto;
        });
        return dtoPage;
    }


    //카페가 아닌곳(음식점)만 반환
    @Transactional
    public Page<RestaurantResponseDTO> searchOnlyRestaurant(String keyword, Pageable pageable) {
        System.out.println("searchOnlyRestaurant 함수 : " + keyword);
        Page<Restaurant> restaurants;

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 문자열인 경우, 모든 카페가 아닌 음식점 검색
            restaurants = restaurantRepository.findAllRestaurant(pageable);
        } else {
            // keyword가 있는 경우, 해당 키워드를 포함하는 카페가 아닌 음식점 검색
            restaurants = restaurantRepository.findRestaurantsExcludingCafes(keyword, pageable);
        }

        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = restaurants.map(restaurant -> {
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            return dto;
        });
        return dtoPage;
    }

    @Transactional
    //기본조회
    public Page<RestaurantResponseDTO> findAll(Pageable pageable) {

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
        Page<RestaurantResponseDTO> dtoPage = restaurantPage.map(restaurant -> {
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            return dto;
        });
        return dtoPage;

    }

    //맛집 하나만 반환
    public RestaurantResponseDTO findById(Long restaurantId, Pageable pageable) {

        return null;
    }

    @Transactional
    //음식점 직선거리
    public Page<RestaurantResponseDTO> findByDistances(String keyword, Float startX, Float startY, Pageable pageable) {
        //TODO 기본적으로 페이지 1에 대해서 출력은 하되 페이지 2부터는 다른 함수로 빼두는 게 좋을 거 같고 dtos를 활용해서 페이지 정보만 가지고 사용하면 됨
        System.out.println("findByDistances들어옴");
        String dong = tmapService.getAddressByCoordinates(startX, startY);
        List<Restaurant> list = null;
        List<RestaurantResponseDTO> dtos = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 문자열인 경우, 모든 카페가 아닌 음식점 검색
            list = restaurantRepository.findAllAddress(dong);
        } else {
            // keyword가 있는 경우, 해당 키워드를 포함하는 카페가 아닌 음식점 검색
            list = restaurantRepository.findAllAddress(dong, keyword);
        }
        for (Restaurant target : list) {
            Double distance = tmapService.calculateDistance(startX, startY, target.getLongitude(), target.getLatitude());
            target.setDistance(distance);

            dtos.add(new RestaurantResponseDTO(target));
        }
        Collections.sort(dtos, Comparator.comparingDouble(RestaurantResponseDTO::getDistance));
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        List<RestaurantResponseDTO> sublist;//페이지에 맞게 짜르기
        if (start >= dtos.size()) {
            // 데이터 범위를 초과하는 페이지 번호의 경우 빈 리스트 반환
            sublist = Collections.emptyList();
        } else {
            sublist = dtos.subList(start, end); // 페이지에 맞게 자르기
        }
        Page<RestaurantResponseDTO> dtoPage = new PageImpl<>(sublist, pageable, dtos.size());
        dtos.clear();
        return dtoPage;
    }


    //위도, 경도 넣어줌
    @Transactional
    public void updateCoordinates() {
        List<Restaurant> list = restaurantRepository.findAll();
        for (Restaurant restaurant : list) {
            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            if (coordinates != null && !coordinates.isEmpty()) {
                restaurant.setCoordinates(coordinates.get("latitude"), coordinates.get("longitude"));
            }
        }
    }

    //음식점 상세
    @Transactional
    public RestaurantResponseDTO showDetails(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(RuntimeException::new);
        Page<ReviewResponseDTO> reviewList = reviewService.showReviewsByRestaurant(id, Pageable.ofSize(5));
        RestaurantResponseDTO responseDTO = new RestaurantResponseDTO(
                restaurant,
                reviewList
        );
        // Return the populated DTO
        return responseDTO;

    }

    //음식점 상세 -> 리뷰폼
    @Transactional
    public RestaurantResponseDTO showDetailsOnlyReviews(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(RuntimeException::new);
        RestaurantResponseDTO responseDTO = new RestaurantResponseDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getRestaurantType(),
                restaurant.getDegree(),
                restaurant.getAddressRoad(),
                restaurant.getAddressNumber(),
                restaurant.getTel(),
                restaurant.getMenus(),
                restaurant.getTotalReviews(),
                restaurant.getTotalTaste(),
                restaurant.getTotalCost(),
                restaurant.getTotalKind(),
                restaurant.getTotalMood(),
                restaurant.getTotalPark()
        );
        // Return the populated DTO
        return responseDTO;
    }

    //개인의 음식점 경로 배열
    @Transactional
    public List<Restaurant> pathList(double startX, double startY, String searchDttm) {
        //1. 사용자의 위치정보를 주소로 반환후 XX동 XX까지 추출
        String userAddress = tmapService.getAddressByCoordinates2(startX, startY);

        //2. 주소로 음식점 검색함
        List<Restaurant> restaurants = restaurantRepository.findByOnlyAddress(userAddress);

        //3. 리뷰평점순으로 20개 추출(1차 필터링)
        restaurants = sortByDegree(restaurants);

        //4. 경로구하기
        restaurants = getPath(startX, startY, restaurants, searchDttm, 0);

        //5. 경로순으로 정렬
        restaurants = sortByPath(restaurants);

        return restaurants;
    }

    //리뷰평점순으로 정렬
    @Transactional
    public List<Restaurant> sortByDegree(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getDegree(), r1.getDegree()))
                .limit(20)
                .collect(Collectors.toList());
    }

    //경로시간 구하기
    @Transactional
    public List<Restaurant> getPath(double startX, double startY, List<Restaurant> restaurants, String searchDttm, int start) {
        for (int i = start; i < restaurants.size(); i++) {
            restaurants.get(i).setPathTime(tmapService.totalTime(Double.toString(startX), Double.toString(startY),
                    Double.toString(restaurants.get(i).getLongitude()), Double.toString(restaurants.get(i).getLatitude()), 0, "json", 10, searchDttm));
        }
        return restaurants;
    }

    //거리순으로 정렬
    @Transactional
    public List<Restaurant> sortByPath(List<Restaurant> restaurants) {
        return restaurants.stream()
                .sorted((r1, r2) -> Double.compare(r2.getPathTime(), r1.getPathTime()))
                .limit(5)
                .collect(Collectors.toList());
    }

    //가중치 배열을 만들기 위한 로직(이미 합친 배열을 가지고왔다는 전제), 사람마다 해줘야함
    @Transactional
    public void getWeightInfo(double startX, double startY, List<Restaurant> mergedList, String searchDttm) {

        //1. 합친배열에서 경로시간을 받아온다
        mergedList = getPath(startX, startY, mergedList, searchDttm, 5);

        //2. 경로시간대로 다시 정렬
        mergedList = sortByPath(mergedList);

        //3. 가중치 배열이랑 비교하기 쉽게 PersonalPath 배열로 변환
        List<PersonalPath> personalPaths = getPersonal(mergedList, 0);

    }

    //상대방 배열과 내 배열 합침 == 채팅 인원수대로 for 돌려야함
    @Transactional
    public List<Restaurant> mergeList(List<Restaurant> restaurants1, List<Restaurant> restaurants2) {
        List<Restaurant> mergedList = new ArrayList<>(restaurants1);
        mergedList.addAll(restaurants2);
        return mergedList;
    }

    //이미 경로순으로 정렬된 restaurants 배열이 들어와야함
    @Transactional
    public List<PersonalPath> getWeight(List<Restaurant> restaurants) {
        return null;
    }

    //가중치 배열 만들기(모든 개인별 배열 넣어줌),
    public List<PersonalPath> getPersonal(List<Restaurant> restaurants, int start) {
        List<PersonalPath> personalPaths = new ArrayList<>();
        for (int i = start; i < restaurants.size() + start; i++) {
            personalPaths.get(i).setRestaurant(restaurants.get(i - start));
            personalPaths.get(i).setTotalTime(restaurants.get(i - start).getPathTime());
            personalPaths.get(i).setSerialNum(serialNum);
            serialNum++;
        }
        return personalPaths;
    }

    /* //주간순위 ->음식종류별
    @Transactional
     public List<RestaurantResponseDTO> getTop5RestaurantsTypeByCount() {
         List<Restaurant> topRestaurants = restaurantRepository.findTop5ByCount();
         return topRestaurants.stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
     }

     // list to dto for 음식종류별 주간순위
     private RestaurantResponseDTO convertToDto(Restaurant restaurant) {
         return new RestaurantResponseDTO(
                 restaurant.getRestaurantType()
         );
     }
 */
    //음식별 순위
    @Transactional
    public List<RestaurantResponseDTO> getTop5RestaurantsByFood() {
        return null;
    }
}