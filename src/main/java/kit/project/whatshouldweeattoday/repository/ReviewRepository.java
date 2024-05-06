package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r JOIN r.restaurant rest WHERE rest.addressNumber LIKE %:address%")
    Page<Review> findByAddress(@Param("address") String address, Pageable pageable);

    //음식점 아이디로 리뷰찾기
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :id")
    List<Review> findByRestaurant(@Param("id") Long id);

    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :id")
    Page<Review> findByRestaurantforPage(@Param("id") Long restaurantId, Pageable pageable);


}
