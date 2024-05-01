package kit.project.whatshouldweeattoday.service;


import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 경우, findAll을 호출
            return findAll(pageable);
        }

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

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 경우, findAll을 호출
            return findAll(pageable);
        }
        // JPA Repository에서 Page<Restaurant>를 반환받음
        Page<Restaurant> onlyCafe = restaurantRepository.findOnlyCafes(keyword, pageable);
        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = onlyCafe.map(restaurant -> {
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

        if (keyword == null || keyword.trim().isEmpty()) {
            // keyword가 null이거나 빈 경우, findAll을 호출
            return findAll(pageable);
        }
        // JPA Repository에서 Page<Restaurant>를 반환받음
        Page<Restaurant> onlyRestaurant = restaurantRepository.findRestaurantsExcludingCafes(keyword, pageable);
        // Page<Restaurant>를 Page<RestaurantResponseDTO>로 변환
        Page<RestaurantResponseDTO> dtoPage = onlyRestaurant.map(restaurant -> {
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
}