package kr.spartaclub.coffeeorder.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.spartaclub.coffeeorder.domain.order.entity.OrderItem;

/**
 * 주문 상세 리포지토리
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * 주문 ID로 주문 상세 목록 조회
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 메뉴 ID로 주문 상세 목록 조회
     */
    List<OrderItem> findByMenuId(Long menuId);

    /**
     * 주문 ID 목록으로 주문 상세 목록 조회
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderId IN :orderIds")
    List<OrderItem> findByOrderIdIn(@Param("orderIds") List<Long> orderIds);
}
