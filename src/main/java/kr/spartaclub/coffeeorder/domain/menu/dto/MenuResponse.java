package kr.spartaclub.coffeeorder.domain.menu.dto;

import java.util.List;

import kr.spartaclub.coffeeorder.domain.menu.entity.Menu;
import kr.spartaclub.coffeeorder.domain.menu.enums.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuResponse {

    private Long menuId;
    private String name;
    private Integer price;
    private String description;
    private List<String> ingredients;
    private MenuStatus status;

    /**
     * Entity -> Response 변환
     */
    public static MenuResponse from(Menu menu) {
        return MenuResponse.builder()
                .menuId(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .description(menu.getDescription())
                .ingredients(menu.getIngredients())
                .status(menu.getStatus())
                .build();
    }
}
