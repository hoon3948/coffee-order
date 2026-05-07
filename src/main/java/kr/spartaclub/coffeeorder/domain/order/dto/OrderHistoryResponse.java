package kr.spartaclub.coffeeorder.domain.order.dto;

import kr.spartaclub.coffeeorder.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 내역 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponse {

    private Long orderId;
    private String menuName;
    private Integer price;
    private LocalDateTime orderTime;

    /**
     * Order 엔티티로부터 응답 생성
     */
    public static OrderHistoryResponse of(Order order, String menuName) {
        return OrderHistoryResponse.builder()
                .orderId(order.getId())
                .menuName(menuName)
                .price(order.getPrice())
                .orderTime(order.getOrderTime())
                .build();
    }
}
