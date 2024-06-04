package kit.project.whatshouldweeattoday.repository;

import kit.project.whatshouldweeattoday.domain.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    //읍,면, 동별로 찾기 -> 리뷰만
    @Query("SELECT r FROM Review r JOIN r.restaurant rest WHERE rest.addressNumber LIKE %:address% ORDER BY r.createdDate DESC")
    Page<Review> findByAddress(@Param("address") String address, Pageable pageable);

    //음식점 아이디로 리뷰찾기
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :id")
    List<Review> findByRestaurant(@Param("id") Long id);

    //음식점 아이디로 리뷰찾기 -> 페이징처리
    @Query("SELECT r FROM Review r WHERE r.restaurant.id = :id")
    Page<Review> findByRestaurantforPage(@Param("id") Long restaurantId, Pageable pageable);

    //각 음식점의 최신 리뷰 날짜 조회
    @Query("SELECT MAX(r.createdDate) FROM Review r WHERE r.restaurant.id = :restaurantId")
    LocalDateTime findLatestReviewDateByRestaurantId(@Param("restaurantId") Long restaurantId);
}

