package kit.project.whatshouldweeattoday.controller;

import io.github.classgraph.Resource;
import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final FoodService foodService;
    private static final String IMAGE_DIRECTORY = "src/main/resources/static/images/";

    @GetMapping("/main/image")
    public String getMainImage(@RequestParam String foodName) throws BadRequestException {
        return foodService.getImageRouteByFoodName(foodName);
    }

    // 음식이미지
    @PatchMapping("/update-image-routes")
    public ResponseEntity<String> updateImageRoutes() {
        foodService.updateImageRoutes();
        return new ResponseEntity<>("Image routes updated successfully", HttpStatus.OK);
    }


    /*@GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(IMAGE_DIRECTORY).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath));

                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }*/
}
