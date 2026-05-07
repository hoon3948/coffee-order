package kr.spartaclub.coffeeorder.domain.point.entity;

import java.time.LocalDateTime;

import kr.spartaclub.coffeeorder.domain.point.enums.PointHistoryType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 이력 엔티티
 */
@Entity
@Table(
        name = "user_point_history",
        indexes = {
                @Index(name = "idx_user_created", columnList = "user_id, created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PointHistoryType type;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    @Column(name = "description", length = 200)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserPointHistory(
            Long userId,
            PointHistoryType type,
            Integer amount,
            Integer balanceAfter,
            String description
    ) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description;
    }

    /**
     * 충전 이력 생성
     */
    public static UserPointHistory createChargeHistory(
            Long userId,
            int amount,
            int balanceAfter,
            String description
    ) {
        return UserPointHistory.builder()
                .userId(userId)
                .type(PointHistoryType.CHARGE)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .description(description)
                .build();
    }

    /**
     * 사용 이력 생성
     */
    public static UserPointHistory createUseHistory(
            Long userId,
            int amount,
            int balanceAfter,
            String description
    ) {
        return UserPointHistory.builder()
                .userId(userId)
                .type(PointHistoryType.USE)
                .amount(-amount)  // 사용은 음수로 저장
                .balanceAfter(balanceAfter)
                .description(description)
                .build();
    }
}
