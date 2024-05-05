package kit.project.whatshouldweeattoday.domain.dto.restaurant;


import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String restaurantType;
    private Double degree;
    private String addressRoad;
    private String addressNumber;
    private String tel;
    private String menus;

    private int totalReviews;
    private int totalTaste;
    private int totalCost;
    private int totalKind;
    private int totalMood;
    private int totalPark;

    private Double latitude;
    private Double longitude;
    private Double distance;
    private Integer pathTime;

    // Entity -> DTO
    public RestaurantResponseDTO(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.restaurantType = restaurant.getRestaurantType();
        this.degree = restaurant.getDegree();
        this.addressRoad = restaurant.getAddressRoad();
        this.addressNumber = restaurant.getAddressNumber();
        this.tel = restaurant.getTel();
        this.menus = restaurant.getMenus();
        this.totalReviews = restaurant.getTotalReviews();
        this.totalTaste = restaurant.getTotalTaste();
        this.totalCost = restaurant.getTotalCost();
        this.totalKind = restaurant.getTotalKind();
        this.totalMood = restaurant.getTotalMood();
        this.totalPark = restaurant.getTotalPark();
        this.longitude = restaurant.getLongitude();
        this.latitude=restaurant.getLatitude();
        this.distance=restaurant.getDistance();
        this.pathTime=restaurant.getPathTime();
    }

}