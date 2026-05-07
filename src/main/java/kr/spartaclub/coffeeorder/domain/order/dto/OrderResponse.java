package kr.spartaclub.coffeeorder.domain.order.dto;

import kr.spartaclub.coffeeorder.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String menuName;
    private Integer price;
    private Integer pointBalance;
    private LocalDateTime orderTime;

    /**
     * Order 엔티티로부터 응답 생성
     */
    public static OrderResponse of(
            Order order,
            String menuName,
            Integer pointBalance
    ) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .menuName(menuName)
                .price(order.getPrice())
                .pointBalance(pointBalance)
                .orderTime(order.getOrderTime())
                .build();
    }
}
