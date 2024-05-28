package kit.project.whatshouldweeattoday.controller;

import kit.project.whatshouldweeattoday.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {

    private final FoodService foodService;

    @GetMapping("/main/image")
    public String getMainImage(@RequestParam String foodName) throws BadRequestException {
        return foodService.getImageRouteByFoodName(foodName);
    }
}
