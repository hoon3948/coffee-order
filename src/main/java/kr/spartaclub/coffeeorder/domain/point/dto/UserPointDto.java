package kr.spartaclub.coffeeorder.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointDto {
    
    private Long userId;
    private Integer balance;
    private Integer totalChargeAmount;
    private Integer totalUseAmount;

    /**
     * 포인트 정보 생성
     */
    public static UserPointDto of(
            Long userId,
            Integer balance,
            Integer totalChargeAmount,
            Integer totalUseAmount
    ) {
        return UserPointDto.builder()
                .userId(userId)
                .balance(balance)
                .totalChargeAmount(totalChargeAmount)
                .totalUseAmount(totalUseAmount)
                .build();
    }
}
