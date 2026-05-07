package kr.spartaclub.coffeeorder.domain.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.spartaclub.coffeeorder.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 포인트 엔티티
 */
@Entity
@Table(name = "user_points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPoint extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    @Builder
    public UserPoint(Long userId, Integer balance) {
        this.userId = userId;
        this.balance = balance != null ? balance : 0;
    }

    /**
     * 포인트 충전
     * @param amount 충전 금액
     * @throws IllegalArgumentException 충전 금액이 0 이하인 경우
     */
    public void charge(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance += amount;
    }

    /**
     * 포인트 사용
     * @param amount 사용 금액
     * @throws IllegalArgumentException 사용 금액이 0 이하이거나 잔액이 부족한 경우
     */
    public void use(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("포인트 잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    /**
     * 잔액 확인
     */
    public boolean hasEnoughBalance(int amount) {
        return this.balance >= amount;
    }
}
