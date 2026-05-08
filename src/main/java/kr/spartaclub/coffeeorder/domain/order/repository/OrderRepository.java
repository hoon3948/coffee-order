package kr.spartaclub.coffeeorder.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.spartaclub.coffeeorder.domain.order.entity.Order;

/**
 * 주문 Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 사용자별 주문 내역 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 주문 목록
     */
    Page<Order> findByUserIdOrderByOrderTimeDesc(Long userId, Pageable pageable);

    /**
     * 사용자별 특정 기간 주문 내역 조회
     * @param userId 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param pageable 페이징 정보
     * @return 주문 목록
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
           "AND o.orderTime BETWEEN :startDate AND :endDate " +
           "ORDER BY o.orderTime DESC")
    Page<Order> findByUserIdAndOrderTimeBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * 특정 기간의 주문 내역 조회
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 주문 목록
     */
    @Query("SELECT o FROM Order o WHERE o.orderTime BETWEEN :startDate AND :endDate")
    List<Order> findByOrderTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * 사용자의 최근 주문 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 주문 목록
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.orderTime DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자의 총 주문 횟수 조회
     * @param userId 사용자 ID
     * @return 주문 횟수
     */
    long countByUserId(Long userId);
}
