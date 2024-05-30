package kit.project.whatshouldweeattoday.service;

import kit.project.whatshouldweeattoday.domain.entity.Food;
import kit.project.whatshouldweeattoday.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FoodService {
    private final FoodRepository foodRepository;
    private static final String IMAGE_DIRECTORY = "src/main/resources/static/images";

    public String getImageRouteByFoodName(String foodName) throws BadRequestException {
        Food food = foodRepository.findByFoodName(foodName)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        return food.getImageRoute();
    }

    public void increaseFoodCount(String foodName) throws BadRequestException {
        Food food = foodRepository.findByFoodName(foodName)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 음식입니다."));
        food.setCount(food.getCount() + 1);
        foodRepository.save(food);
    }
    public Optional<Food> findByFoodName(String foodName) {
        return foodRepository.findByFoodName(foodName);
    }
    //=========================이미지 업로드=================================

    public void updateImageRoutes() {
        try {
            Files.list(Paths.get(IMAGE_DIRECTORY))
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        String foodName = fileName.replace(".jpg", "");
                        Optional<Food> foodOpt = foodRepository.findByFoodName(foodName);

                        if (foodOpt.isPresent()) {
                            Food food = foodOpt.get();
                            String encodedFoodName = null;
                            try {
                                encodedFoodName = URLEncoder.encode(foodName, StandardCharsets.UTF_8.toString());
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                            String imageUrl = "https://storage.googleapis.com/food_img/" + encodedFoodName + ".jpg";
                            food.setImageRoute(imageUrl);
                            foodRepository.save(food);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Failed to update image routes", e);
        }
    }

}
