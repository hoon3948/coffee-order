package kr.spartaclub.coffeeorder.domain.point.dto;

import kr.spartaclub.coffeeorder.domain.point.entity.UserPointHistory;
import kr.spartaclub.coffeeorder.domain.point.enums.PointHistoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 이력 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointHistoryDto {
    
    private Long historyId;
    private Long userId;
    private PointHistoryType type;
    private Integer amount;
    private Integer balanceAfter;
    private String description;
    private LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     */
    public static UserPointHistoryDto from(UserPointHistory history) {
        return UserPointHistoryDto.builder()
                .historyId(history.getId())
                .userId(history.getUserId())
                .type(history.getType())
                .amount(history.getAmount())
                .balanceAfter(history.getBalanceAfter())
                .description(history.getDescription())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
