package kr.spartaclub.coffeeorder.domain.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import kr.spartaclub.coffeeorder.domain.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 내역 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {

    private Long orderId;
    private Integer totalPrice;
    private LocalDateTime orderTime;
    private List<OrderItemInfo> items;

    /**
     * Order 엔티티로부터 OrderHistoryResponse 생성
     */
    public static OrderHistoryResponse of(Order order, List<OrderItemInfo> items) {
        return OrderHistoryResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .orderTime(order.getOrderTime())
                .items(items)
                .build();
    }

    /**
     * 주문 항목 정보 DTO
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemInfo {
        private String menuName;
        private Integer quantity;
        private Integer price;

        public static OrderItemInfo of(String menuName, Integer quantity, Integer price) {
            return OrderItemInfo.builder()
                    .menuName(menuName)
                    .quantity(quantity)
                    .price(price)
                    .build();
        }
    }
}
