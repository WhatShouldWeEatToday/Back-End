package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.domain.dto.MsgResponseDTO;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import kit.project.whatshouldweeattoday.domain.type.ReviewType;
import kit.project.whatshouldweeattoday.service.OcrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OcrController {

    @Value("${naver.service.secretKey}")
    private String secretKey;
    private final OcrService ocrService;


    // 파일 업로드 및 OCR 수행을 위한 POST 요청 핸들러 메서드
    @PostMapping("/api/review/receipt")
    public ResponseEntity<MsgResponseDTO> uploadAndOcr(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("파일이 비어있습니다");
        }

        File tempFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(tempFile);

        List<String> result = ocrService.callAPI("POST", tempFile.getPath(), secretKey, "jpeg");
        Review.builder()
                .reviewType(ReviewType.CERTIFY)
                .build();
        for(String s : result){
                log.info(s);
        }
        tempFile.delete(); // 임시 파일 삭제
        return ResponseEntity.ok(new MsgResponseDTO("영수증 인증 완료", HttpStatus.OK.value()));
    }

    private static void convertByRegex(List<String> result, List<String> convertResult) {
        for(String s : result){
            s = s.replaceAll("[<>&\"'/: ]","");
            convertResult.add(s);
        }
    }
}
