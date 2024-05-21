package kit.project.whatshouldweeattoday.controller;

import com.fasterxml.jackson.databind.JsonNode;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PathResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.restaurant.PathRequestDTO;
import kit.project.whatshouldweeattoday.service.RestaurantService;
import kit.project.whatshouldweeattoday.service.TMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final TMapService tmapService;

    //맛집 검색
    @GetMapping("/search")
    public ResponseEntity<Page<RestaurantResponseDTO>> search(@RequestParam(name = "word", required = false) String word,
                                                              @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //음식점 상세페이지 -> 기본
    @GetMapping("/{restaurantId}/details")
    public ResponseEntity<RestaurantResponseDTO> showDetails(@PathVariable("restaurantId") Long id) {
        RestaurantResponseDTO dto = restaurantService.showDetails(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //음식점 상세페이지 -> 리뷰등록폼
    @GetMapping("/{restaurantId}/review")
    public ResponseEntity<RestaurantResponseDTO> showDetailsOnlyReviews(@PathVariable("restaurantId") Long id) {
        RestaurantResponseDTO dto = restaurantService.showDetailsOnlyReviews(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
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
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByDegree(@RequestParam(name = "word", required = false) String word,
                                                                              @PageableDefault(sort = "degree", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집 조회(리뷰개수순(total_reviews))
    @GetMapping("/search/reviews")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByTotalReviews(@RequestParam(name = "word", required = false) String word,
                                                                                    @PageableDefault(sort = "totalReviews", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchRestaurants(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집조회(음식점만)
    @GetMapping("/search/onlyrestaurants")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyRestaurant(@RequestParam(name = "word", required = false) String word,
                                                                          @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyRestaurant(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    //맛집조회(카페만) ==> 카페,제과,베이커리,케이크,커피
    @GetMapping("/search/onlycafes")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyCafe(@RequestParam(name = "word", required = false) String word,
                                                                    @PageableDefault(sort = "id", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyCafes(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //맛집조회 직선거리순-> 출발지 위도,경도 및 도착지 위도,경도 필요
    @GetMapping("/search/routes")
    public ResponseEntity<Page<RestaurantResponseDTO>> getRestaurantsByRoutes(
            @RequestParam(name = "word", required = false) String word,
            @RequestParam(name = "startX") Float startX,
            @RequestParam(name = "startY") Float startY,
            @PageableDefault(sort = "distance", direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        // 거리를 기준으로 정렬하기 위해 Pageable 객체를 생성할 때, distance를 기준으로 정렬되도록 설정
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("distance"));

        Page<RestaurantResponseDTO> page = restaurantService.findByDistances(word, startX, startY, sortedPageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // 맛집 위도 경도 넣어주기
    @PatchMapping("/api/initCoordinates")
    public String initCoordinates() {
        restaurantService.updateCoordinates();
        return null;
    }

    //위치 받아서 주소 반환
    @GetMapping("/reverseGeo")
    public String getAddressByCoordinates(double startX, double startY) {

        return tmapService.getAddressByCoordinates(startX, startY);
    }

    //리뷰개수순으로 정렬하면서 카페만
    @GetMapping("/search/onlycafes/reviews")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyCafeforTotalReviews(@RequestParam(name = "word", required = false) String word,
                                                                                   @PageableDefault(sort = "totalReviews", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyCafes(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //평점순으로 정렬하면서 카페만
    @GetMapping("/search/onlycafes/degree")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyCafeforDegree(@RequestParam(name = "word", required = false) String word,
                                                                             @PageableDefault(sort = "degree", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyCafes(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //리뷰개수순으로 정렬하면서 음식점만
    @GetMapping("/search/onlyrestaurants/reviews")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyRestaurantforTotalReviews(@RequestParam(name = "word", required = false) String word,
                                                                                         @PageableDefault(sort = "totalReviews", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyRestaurant(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //평점으로 정렬하면서 음식점만
    @GetMapping("/search/onlyrestaurants/degree")
    public ResponseEntity<Page<RestaurantResponseDTO>> findOnlyRestaurantforDegree(@RequestParam(name = "word", required = false) String word,
                                                                                   @PageableDefault(sort = "degree", direction = Sort.Direction.DESC, size = 10) Pageable pageable) {
        Page<RestaurantResponseDTO> page = restaurantService.searchOnlyRestaurant(word, pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    //음식점 경로 시간 알아보기
    @PostMapping("/search/totalTime")
    public ResponseEntity<?> totalTime(
            @RequestBody PathRequestDTO totalTimeRequest) {

        try {
            int totalTime = tmapService.totalTime(totalTimeRequest.getStartX(), totalTimeRequest.getStartY(), totalTimeRequest.getEndX(), totalTimeRequest.getEndY(), totalTimeRequest.getLang(), totalTimeRequest.getFormat(), totalTimeRequest.getCount(), totalTimeRequest.getSearchDttm());
            return ResponseEntity.ok(totalTime);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error calculating total time: " + e.getMessage());
        }
    }

    //주소받아서 음식점경로 알아오기
   /* @PostMapping("/search/totalPath")
    public ResponseEntity<PathResponseDTO> getTransitRoute(
            @RequestBody PathRequestDTO totalTimeRequest) {
        String departure = totalTimeRequest.getDeparture();
        String destination = totalTimeRequest.getDestination();
        String searchDttm = totalTimeRequest.getSearchDttm();

        PathResponseDTO routeInfo = tmapService.getTransitRoute(departure, destination, 0, "json", 1, searchDttm);

        if (routeInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(routeInfo);
    }
*/
    @PostMapping("/search/totalPath")
    public ResponseEntity<JsonNode> getTransitRoute(@RequestBody PathRequestDTO totalTimeRequest) {
        String departure = totalTimeRequest.getDeparture();
        String destination = totalTimeRequest.getDestination();
        String searchDttm = totalTimeRequest.getSearchDttm();

        JsonNode routeInfo = tmapService.getJsonByTransitRoute(departure, destination, 0, "json", 1, searchDttm);

        if (routeInfo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        return ResponseEntity.ok(routeInfo);
    }
}




