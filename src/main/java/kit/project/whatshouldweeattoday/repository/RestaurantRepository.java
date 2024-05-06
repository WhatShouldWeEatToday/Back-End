package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Restaurant;
import kit.project.whatshouldweeattoday.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    //카페만 조회
    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%'")
    Page<Restaurant> findAllCafes(Pageable pageable);

    //음식점만 조회
    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%'")
    Page<Restaurant> findAllRestaurant(Pageable pageable);

    //주소로 조회
    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%')")
    List<Restaurant> findAllAddress(@Param("dong") String dong);

    //주소와 키워드로 조회
    @Query(value = "SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') and r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%')")
    List<Restaurant> findAllAddress(@Param("dong") String dong, @Param("keyword") String keyword);

    //직선거리순으로 정렬하면서 음식점만 반환
    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') AND r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%'")
    List<Restaurant> findAllRestaurantforDistance(@Param("dong") String dong, Pageable pageable);

    //직선거리순으로 정렬하면서 카페만 반환
    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') AND r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%'")
    List<Restaurant> findAllCafesforDistance(@Param("dong") String dong, Pageable pageable);


    //리뷰목록에서 읍면동 별로 리뷰 및 음식점 찾기
    @Query("SELECT r FROM Restaurant r JOIN r.reviewList rev WHERE r.addressNumber LIKE CONCAT('%', :address, '%') GROUP BY r.id ORDER BY MAX(rev.createdDate) DESC")
    Page<Restaurant> findByAddress(String address, Pageable pageable);

    //최신순 리뷰 있는 음식점부터 조회
    @Query("SELECT r FROM Restaurant r WHERE r.id IN " +
            "(SELECT rev.restaurant.id FROM Review rev GROUP BY rev.restaurant.id " +
            "ORDER BY MAX(rev.createdDate) DESC)")
    Page<Restaurant> findAllByReviewCreated(Pageable pageable);
}
