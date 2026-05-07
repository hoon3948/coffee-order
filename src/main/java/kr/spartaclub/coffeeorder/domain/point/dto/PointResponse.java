package kr.spartaclub.coffeeorder.domain.point.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponse {

    private Long userId;
    private Integer balance;
    private String message;

    /**
     * 포인트 잔액 응답 생성
     */
    public static PointResponse of(Long userId, Integer balance) {
        return PointResponse.builder()
                .userId(userId)
                .balance(balance)
                .build();
    }

    /**
     * 포인트 충전/사용 응답 생성
     */
    public static PointResponse of(Long userId, Integer balance, String message) {
        return PointResponse.builder()
                .userId(userId)
                .balance(balance)
                .message(message)
                .build();
    }
}
