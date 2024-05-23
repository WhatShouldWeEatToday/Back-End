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

    //-> restaurant -> restaurnatDTO (reviewList를 추출하기 위한 용도)
    @Transactional(readOnly = true)
    public RestaurantResponseDTO getRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return toDto(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponseDTO> getRestaurantsByKeywordAndAddress(String keyword, String address) {
        List<Restaurant> restaurants = restaurantRepository.findByKeywordAndAddress(keyword, address);
        return restaurants.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RestaurantResponseDTO toDto(Restaurant restaurant) {
        Page<ReviewResponseDTO> reviewPage = new PageImpl<>(
                restaurant.getReviewList().stream()
                        .map(ReviewResponseDTO::new)
                        .collect(Collectors.toList())
        );
        return new RestaurantResponseDTO(restaurant, reviewPage);
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