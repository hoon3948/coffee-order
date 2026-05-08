package kr.spartaclub.coffeeorder.domain.order.entity;

import jakarta.persistence.*;
import kr.spartaclub.coffeeorder.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상세 엔티티
 */
@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    /**
     * 주문 상세 생성
     */
    public static OrderItem create(Long orderId, Long menuId, Integer quantity, Integer price) {
        OrderItem orderItem = new OrderItem();
        orderItem.orderId = orderId;
        orderItem.menuId = menuId;
        orderItem.quantity = quantity;
        orderItem.price = price;
        return orderItem;
    }

    /**
     * 소계 계산 (단가 * 수량)
     */
    public int getSubtotal() {
        return price * quantity;
    }
}
