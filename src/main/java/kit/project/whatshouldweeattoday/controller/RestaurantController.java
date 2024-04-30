package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.user.signup.SignupResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.service.RestaurantService;
import kit.project.whatshouldweeattoday.service.TMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurant")
public class RestaurantController {

     private final RestaurantService restaurantService;

     //맛집 검색
    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantResponseDTO>> search(String word,
                                                              @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //그냥 조회
    @GetMapping("/findAll")
    public ResponseEntity<Page<RestaurantResponseDTO>> findAll(
                                                              @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        System.out.println("findAll 함수 들어옴");
        Page<RestaurantResponseDTO> page = restaurantService.findAll(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집 조회(리뷰평점순(degree))
    @GetMapping("/search/degree")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByDegree(String word,
                                                                              @PageableDefault(sort = "degree", direction = Sort.Direction.DESC, size = 10)Pageable pageable ){
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집 조회(리뷰개수순(total_reviews))
    @GetMapping("/search/reviews")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByTotalReviews(String word,
                                                                                    @PageableDefault(sort = "totalReviews", direction = Sort.Direction.DESC, size = 15)Pageable pageable ){
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집조회(음식점만)
    @GetMapping("/search/onlyrestaurants")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyRestaurant(String word,
                                                                          @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable){
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyRestaurant(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    //맛집조회(카페만) ==> 카페,제과,베이커리,케이크,커피
    @GetMapping("/search/onlycafes")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyCafe(String word,
                                                                    @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable){
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyCafes(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


}

