package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.restaurant.RestaurantResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewRequestDTO;
import kit.project.whatshouldweeattoday.domain.dto.review.ReviewResponseDTO;
import kit.project.whatshouldweeattoday.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰등록 -> 작성하기 버튼 눌렀을때
    @PostMapping("/api/review/{restaurantId}")
    public ResponseEntity<MsgResponseDTO> save(@PathVariable("restaurantId") Long restaurantId, @RequestBody ReviewRequestDTO requestDTO) {
        requestDTO.setTotalLikes(0L);
        reviewService.save(restaurantId,requestDTO);
        return ResponseEntity.ok(new MsgResponseDTO("리뷰 등록 완료", HttpStatus.OK.value()));
    }

    // 최신순 리뷰 조회(default)
    @GetMapping("/review/findAll")
    public ResponseEntity<Page<RestaurantResponseDTO>> findAll(@RequestParam(name = "address", required = false) String address,
                                                               Pageable pageable){
        Page<RestaurantResponseDTO> responseDTOS = reviewService.findAll(address,pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    // 리뷰 수정
    @PatchMapping("/api/review/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable("reviewId") Long id, @RequestBody ReviewRequestDTO requestDTO) {
        return new ResponseEntity<>(reviewService.update(id, requestDTO), HttpStatus.OK);
    }

    // 리뷰 삭제
    @DeleteMapping("/api/review/{reviewId}")
    public ResponseEntity<MsgResponseDTO> delete(@PathVariable("reviewId") Long id) {
        return new ResponseEntity<>(reviewService.delete(id), HttpStatus.OK);
    }

    // 리뷰 수정 및 상세화면
    @GetMapping("/{restaurantId}/review/{id}")
    public ResponseEntity<ReviewResponseDTO> reviewDetail(@PathVariable("restaurantId") Long restaurantId,@PathVariable("id") Long id){
        ReviewResponseDTO responseDTOS = reviewService.reviewDetails(restaurantId,id);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    // 리뷰 읍,면,동 조회
    @GetMapping("/review/findbyAddress/{address}")
    public ResponseEntity<Page<ReviewResponseDTO>> findByAddress(@PathVariable("address") String address, @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 10)Pageable pageable){
        Page<ReviewResponseDTO> responseDTOS = reviewService.findByAdddress(address, pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }
}
