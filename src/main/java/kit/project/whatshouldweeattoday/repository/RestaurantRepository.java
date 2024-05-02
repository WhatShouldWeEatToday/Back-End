package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(Long id);

    @Override
    Page<Restaurant> findAll(Pageable pageable);
    //입력받은 키워드가 메뉴나 점포명으로 필터링(like로 필터링)
    @Query("SELECT r FROM Restaurant r WHERE r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%')")
    Page<Restaurant> findByMenuContainingOrNameContaining(@Param("keyword") String keyword, Pageable pageable);
    //입력받은 키워드가 메뉴나 점포명으로 필터링(like로 필터링)하고 restaurant_type이 카페인곳만 반환
    @Query("SELECT r FROM Restaurant r WHERE (r.name LIKE CONCAT('%',:keyword,'%') OR r.menus LIKE CONCAT('%',:keyword,'%')) AND (r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%' )")
    Page<Restaurant> findOnlyCafes(@Param("keyword") String keyword, Pageable pageable);
    //입력받은 키워드가 메뉴나 점포명으로 필터링(like로 필터링)하고 restaurant_type이 카페가 아닌곳들만 반환
    @Query("SELECT r FROM Restaurant r WHERE (r.name LIKE CONCAT('%',:keyword,'%') OR r.menus LIKE CONCAT('%',:keyword,'%')) AND (r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%')")
    Page<Restaurant> findRestaurantsExcludingCafes(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%'")
    Page<Restaurant> findAllCafes(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%'")
    Page<Restaurant> findAllRestaurant(Pageable pageable);
}
