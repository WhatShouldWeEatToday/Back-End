package kit.project.whatshouldweeattoday.controller;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //리뷰등록
    @PostMapping("/api/review")
    public ResponseEntity<ReviewResponseDTO> save(@RequestBody ReviewRequestDTO requestDTO, HttpSession session) {
        //음식점정보 넣어주기
        
        return new ResponseEntity<>(reviewService.save(requestDTO), HttpStatus.OK);
    }

    //최신순 리뷰 조회
    @GetMapping("/review/findAll")
    public ResponseEntity<Page<ReviewResponseDTO>> findAll(@PageableDefault(sort = "createDate", direction = Sort.Direction.DESC, size = 15)Pageable pageable){
        Page<ReviewResponseDTO> responseDTOS = reviewService.findAll(pageable);

        return new ResponseEntity<>(responseDTOS, HttpStatus.OK);
    }

}
