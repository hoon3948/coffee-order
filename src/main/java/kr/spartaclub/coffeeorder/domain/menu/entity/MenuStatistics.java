package kr.spartaclub.coffeeorder.domain.menu.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.spartaclub.coffeeorder.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 통계 엔티티
 */
@Entity
@Table(
        name = "menu_statistics",
        indexes = {
                @Index(name = "idx_order_date", columnList = "order_date")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_menu_date", columnNames = {"menu_id", "order_date"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuStatistics extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long id;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "order_count", nullable = false)
    private Integer orderCount;

    @Builder
    public MenuStatistics(Long menuId, LocalDate orderDate, Integer orderCount) {
        this.menuId = menuId;
        this.orderDate = orderDate;
        this.orderCount = orderCount != null ? orderCount : 0;
    }

    /**
     * 주문 횟수 증가
     */
    public void incrementOrderCount() {
        this.orderCount++;
    }

    /**
     * 주문 횟수 증가 (특정 개수만큼)
     */
    public void incrementOrderCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("증가 개수는 0보다 커야 합니다.");
        }
        this.orderCount += count;
    }

    /**
     * 오늘 날짜의 통계 생성
     */
    public static MenuStatistics createToday(Long menuId) {
        return MenuStatistics.builder()
                .menuId(menuId)
                .orderDate(LocalDate.now())
                .orderCount(1)
                .build();
    }

    /**
     * 특정 날짜의 통계 생성
     */
    public static MenuStatistics create(Long menuId, LocalDate date) {
        return MenuStatistics.builder()
                .menuId(menuId)
                .orderDate(date)
                .orderCount(1)
                .build();
    }
}
