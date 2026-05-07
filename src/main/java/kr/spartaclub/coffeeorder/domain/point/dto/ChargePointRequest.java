package kr.spartaclub.coffeeorder.domain.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 충전 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChargePointRequest {

    @NotNull(message = "충전 금액은 필수입니다.")
    @Min(value = 1000, message = "최소 충전 금액은 1,000원입니다.")
    private Integer amount;
}
