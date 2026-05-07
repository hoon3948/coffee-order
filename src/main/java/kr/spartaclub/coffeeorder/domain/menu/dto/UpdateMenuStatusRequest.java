package kr.spartaclub.coffeeorder.domain.menu.dto;

import jakarta.validation.constraints.NotNull;
import kr.spartaclub.coffeeorder.domain.menu.enums.MenuStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 상태 변경 요청 DTO (관리자용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuStatusRequest {

    @NotNull(message = "상태는 필수입니다.")
    private MenuStatus status;
}
