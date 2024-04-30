package kit.project.whatshouldweeattoday.controller;

import jakarta.servlet.http.HttpSession;
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

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰등록
    @PostMapping("/api/review/{restaurantId}")
    public ResponseEntity<ReviewResponseDTO> save(@PathVariable Long restaurantId, @RequestBody ReviewRequestDTO requestDTO) {
        requestDTO.setTotalLikes(0L);
        return new ResponseEntity<>(reviewService.save(restaurantId,requestDTO), HttpStatus.OK);
    }

    //최신순 리뷰 조회
    @GetMapping("/review/findAll")
    public ResponseEntity<Page<ReviewResponseDTO>> findAll(@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 15)Pageable pageable){
        Page<ReviewResponseDTO> responseDTOS = reviewService.findAll(pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    //리뷰 수정
    @PatchMapping("/api/review/{id}")
    public ResponseEntity<ReviewResponseDTO> update(@PathVariable Long id, @RequestBody ReviewRequestDTO requestDTO) {
        return new ResponseEntity<>(reviewService.update(id, requestDTO), HttpStatus.OK);
    }

    //리뷰 삭제
    @DeleteMapping("/api/review/{id}")
    public ResponseEntity<MsgResponseDTO> delete(@PathVariable Long id) {
        return new ResponseEntity<>(reviewService.delete(id), HttpStatus.OK);
    }

    //리뷰 수정 및 상세화면
    @GetMapping("/review/{id}")
    public ResponseEntity<ReviewResponseDTO> reviewDetail(@PathVariable Long id){
        ReviewResponseDTO responseDTOS = reviewService.reviewDetails(id);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

    //리뷰 읍,면,동 조회
    @GetMapping("/review/findbyAddress/{address}")
    public ResponseEntity<Page<ReviewResponseDTO>> findByAddress(@PathVariable String address,@PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC, size = 15)Pageable pageable){
        Page<ReviewResponseDTO> responseDTOS = reviewService.findByAdddress(address, pageable);
        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }
}
