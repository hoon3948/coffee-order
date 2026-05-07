package kr.spartaclub.coffeeorder.domain.menu.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 수정 요청 DTO (관리자용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuRequest {

    @Size(max = 50, message = "메뉴명은 50자 이하여야 합니다.")
    private String name;

    @Min(value = 100, message = "가격은 100원 이상이어야 합니다.")
    private Integer price;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    private List<String> ingredients;
}
