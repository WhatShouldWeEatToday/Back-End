package kit.project.whatshouldweeattoday.service;


import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final TMapService tmapService;

    //keyword로 맛집을 검색함(메뉴명, 점포명)
    @Transactional
    public Page<RestaurantResponseDTO> searchRestaurants(String keyword, Pageable pageable) {
        System.out.println("searchRestaurants 함수 : " + keyword);
        // JPA Repository에서 Page<Restaurant>를 반환받음
        Page<Restaurant> restaurantPage = restaurantRepository.findByMenuContainingOrNameContaining(keyword, pageable);

        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = restaurantPage.map(restaurant -> {
            // 좌표 변환 로직
            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            // RestaurantResponseDTO 생성 및 좌표 설정
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            if (coordinates != null && !coordinates.isEmpty()) {
                dto.setLatitude(coordinates.get("latitude"));
                dto.setLongitude(coordinates.get("longitude"));
                System.out.println("좌표정보 : " + dto.getLatitude() + " " + dto.getLongitude());
            }
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
            // 좌표 변환 로직
            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            // RestaurantResponseDTO 생성 및 좌표 설정
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            if (coordinates != null && !coordinates.isEmpty()) {
                dto.setLatitude(coordinates.get("latitude"));
                dto.setLongitude(coordinates.get("longitude"));
            }
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
            // 좌표 변환 로직
            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            // RestaurantResponseDTO 생성 및 좌표 설정
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            if (coordinates != null && !coordinates.isEmpty()) {
                dto.setLatitude(coordinates.get("latitude"));
                dto.setLongitude(coordinates.get("longitude"));
            }
            return dto;
        });
        return dtoPage;
    }

    //기본조회
    public Page<RestaurantResponseDTO> findAll(Pageable pageable) {

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(pageable);
        Page<RestaurantResponseDTO> dtoPage = restaurantPage.map(restaurant -> {
            // 좌표 변환 로직
            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            // RestaurantResponseDTO 생성 및 좌표 설정
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            if (coordinates != null && !coordinates.isEmpty()) {
                dto.setLatitude(coordinates.get("latitude"));
                dto.setLongitude(coordinates.get("longitude"));
            }
            return dto;
        });
        return dtoPage;

    }

    //맛집 하나만 반환
    public RestaurantResponseDTO findById(Long restaurantId, Pageable pageable) {

        return null;
    }

    //음식점 직선거리
    public Page<RestaurantResponseDTO> findByDistances(String keyword, double startX, double startY,Pageable pageable) {
        System.out.println("findByDistances들어옴");
        Page<Restaurant> restaurants;

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 문자열인 경우, 모든 음식점 반환
            restaurants = restaurantRepository.findAll(pageable);
        } else {
            // keyword가 있는 경우, 해당 키워드를 포함하는 음식점 반환
            restaurants = restaurantRepository.findByMenuContainingOrNameContaining(keyword, pageable);
        }
        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = restaurants.map(restaurant -> {

            Map<String, Double> coordinates = tmapService.getCoordinates(restaurant.getAddressRoad());
            // RestaurantResponseDTO 생성 및 좌표 설정
            RestaurantResponseDTO dto = new RestaurantResponseDTO(restaurant);
            if (coordinates != null && !coordinates.isEmpty()) {
                dto.setLatitude(coordinates.get("latitude"));
                dto.setLongitude(coordinates.get("longitude"));
            }
            double endX=dto.getLongitude();
            double endY=dto.getLatitude();
            // 직선거리 구하기
            double distance = tmapService.getDirectDistance(startX,startY,endX,endY);
            // RestaurantResponseDTO 생성 및 좌표 설정
            if (distance != 0.0) {
                dto.setDistance(distance);
            }
            return dto;
        });
        Sort sort = Sort.by("distance").ascending();
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return dtoPage;
    }
}