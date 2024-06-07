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

    boolean existsByName(String name);

    Optional<Restaurant> findById(Long id);

    @Override
    Page<Restaurant> findAll(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%')")
    Page<Restaurant> findByMenuContainingOrNameContaining(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE (r.name LIKE CONCAT('%',:keyword,'%') OR r.menus LIKE CONCAT('%',:keyword,'%')) AND (r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%' )")
    Page<Restaurant> findOnlyCafes(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE (r.name LIKE CONCAT('%',:keyword,'%') OR r.menus LIKE CONCAT('%',:keyword,'%')) AND (r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%')")
    Page<Restaurant> findRestaurantsExcludingCafes(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%'")
    Page<Restaurant> findAllCafes(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%'")
    Page<Restaurant> findAllRestaurant(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%')")
    List<Restaurant> findAllAddress(@Param("dong") String dong);

    @Query(value = "SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') and r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%')")
    List<Restaurant> findAllAddress(@Param("dong") String dong, @Param("keyword") String keyword);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') AND r.restaurantType NOT LIKE '%카페%' AND r.restaurantType NOT LIKE '%커피%' AND r.restaurantType NOT LIKE '%베이커리%'")
    List<Restaurant> findAllRestaurantforDistance(@Param("dong") String dong, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :dong, '%') AND r.restaurantType LIKE '%카페%' OR r.restaurantType LIKE '%커피%' OR r.restaurantType LIKE '%베이커리%'")
    List<Restaurant> findAllCafesforDistance(@Param("dong") String dong, Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN r.reviewList rev WHERE r.addressNumber LIKE CONCAT('%', :address, '%') GROUP BY r.id ORDER BY MAX(rev.createdDate) DESC")
    Page<Restaurant> findByAddress(@Param("address") String address, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.id IN (SELECT rev.restaurant.id FROM Review rev GROUP BY rev.restaurant.id ORDER BY MAX(rev.createdDate) DESC)")
    Page<Restaurant> findAllByReviewCreated(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :userAddress, '%')")
    List<Restaurant> findByOnlyAddress(@Param("userAddress") String userAddress);

    @Query("SELECT r FROM Restaurant r ORDER BY r.count DESC")
    List<Restaurant> findTop5ByCount();

    @Query("SELECT r FROM Restaurant r WHERE r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%')")
    List<Restaurant> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :address, '%') AND (r.name LIKE CONCAT('%', :keyword, '%') OR r.menus LIKE CONCAT('%', :keyword, '%'))")
    List<Restaurant> findByKeywordAndAddress(@Param("keyword") String keyword, @Param("address") String address);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :address, '%')")
    Page<Restaurant> findByAddressForReview(@Param("address") String address, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.addressRoad LIKE CONCAT('%', :address, '%')")
    Page<Restaurant> findByAddressOrderByLatestReview(@Param("address") String address, Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.addressNumber LIKE CONCAT('%', :address, '%')")
    List<Restaurant> findByAddressLatestReview(@Param("address") String address);

    List<Restaurant> findByRestaurantTypeIn(List<String> restaurantTypes);
}
