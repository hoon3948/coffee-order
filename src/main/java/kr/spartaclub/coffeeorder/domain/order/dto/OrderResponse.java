package kr.spartaclub.coffeeorder.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.spartaclub.coffeeorder.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private Integer totalPrice;
    private Integer remainingBalance;
    private LocalDateTime orderTime;
    private List<OrderItemResponse> items;

    /**
     * Order 엔티티로부터 OrderResponse 생성
     */
    public static OrderResponse of(Order order, List<OrderItemResponse> items, Integer remainingBalance) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .remainingBalance(remainingBalance)
                .orderTime(order.getOrderTime())
                .items(items)
                .build();
    }

    /**
     * 주문 항목 응답 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long menuId;
        private String menuName;
        private Integer quantity;
        private Integer price;
        private Integer subtotal;

        public static OrderItemResponse of(Long menuId, String menuName, Integer quantity, Integer price) {
            return OrderItemResponse.builder()
                    .menuId(menuId)
                    .menuName(menuName)
                    .quantity(quantity)
                    .price(price)
                    .subtotal(price * quantity)
                    .build();
        }
    }
}
