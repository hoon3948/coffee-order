package kr.spartaclub.coffeeorder.domain.order.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 엔티티
 */
@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_user_order_time", columnList = "user_id, order_time"),
                @Index(name = "idx_order_time", columnList = "order_time")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Order(Long userId, Integer totalPrice, LocalDateTime orderTime) {
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.orderTime = orderTime != null ? orderTime : LocalDateTime.now();
    }

    /**
     * 주문 생성 팩토리 메서드
     */
    public static Order create(Long userId, Integer totalPrice) {
        return Order.builder()
                .userId(userId)
                .totalPrice(totalPrice)
                .orderTime(LocalDateTime.now())
                .build();
    }
}
