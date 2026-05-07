package kr.spartaclub.coffeeorder.domain.point.dto;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPointHistory;
import kr.spartaclub.coffeeorder.domain.point.enums.PointHistoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 이력 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {

    private Long historyId;
    private PointHistoryType type;
    private Integer amount;
    private Integer balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Entity -> Response 변환
     */
    public static PointHistoryResponse from(UserPointHistory history) {
        return PointHistoryResponse.builder()
                .historyId(history.getId())
                .type(history.getType())
                .amount(history.getAmount())
                .balanceAfter(history.getBalanceAfter())
                .description(history.getDescription())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
