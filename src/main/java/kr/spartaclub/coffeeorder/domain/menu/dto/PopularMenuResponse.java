package kr.spartaclub.coffeeorder.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인기 메뉴 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularMenuResponse {

    private Long menuId;
    private String name;
    private Integer price;
    private Long orderCount;
    private Integer rank;

    /**
     * 인기 메뉴 응답 생성
     */
    public static PopularMenuResponse of(
            Long menuId,
            String name,
            Integer price,
            Long orderCount,
            Integer rank
    ) {
        return PopularMenuResponse.builder()
                .menuId(menuId)
                .name(name)
                .price(price)
                .orderCount(orderCount)
                .rank(rank)
                .build();
    }

    /**
     * Menu 엔티티와 주문 횟수로 인기 메뉴 응답 생성
     */
    public static PopularMenuResponse from(
            kr.spartaclub.coffeeorder.domain.menu.entity.Menu menu,
            Long orderCount,
            Integer rank
    ) {
        return PopularMenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .orderCount(orderCount)
                .rank(rank)
                .build();
    }
}
