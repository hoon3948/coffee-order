package kr.spartaclub.coffeeorder.domain.menu.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.spartaclub.coffeeorder.global.common.BaseTimeEntity;
import kr.spartaclub.coffeeorder.domain.menu.enums.MenuStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메뉴 엔티티
 */
@Entity
@Table(
        name = "menus",
        indexes = {
                @Index(name = "idx_status_deleted", columnList = "status, deleted_at")
        }
)
@SQLDelete(sql = "UPDATE menus SET deleted_at = NOW() WHERE menu_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Convert(converter = StringListConverter.class)
    @Column(name = "ingredients", columnDefinition = "JSON")
    private List<String> ingredients;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MenuStatus status;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Menu(
            String name,
            Integer price,
            String description,
            List<String> ingredients,
            MenuStatus status
    ) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.ingredients = ingredients;
        this.status = status != null ? status : MenuStatus.AVAILABLE;
    }

    /**
     * 메뉴 정보 수정
     */
    public void update(String name, Integer price, String description, List<String> ingredients) {
        if (name != null) {
            this.name = name;
        }
        if (price != null) {
            if (price <= 0) {
                throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
            }
            this.price = price;
        }
        if (description != null) {
            this.description = description;
        }
        if (ingredients != null) {
            this.ingredients = ingredients;
        }
    }

    /**
     * 메뉴 상태 변경
     */
    public void changeStatus(MenuStatus status) {
        this.status = status;
    }

    /**
     * 품절 처리
     */
    public void markAsSoldOut() {
        this.status = MenuStatus.SOLD_OUT;
    }

    /**
     * 판매 재개
     */
    public void markAsAvailable() {
        this.status = MenuStatus.AVAILABLE;
    }

    /**
     * 판매 가능 여부 확인
     */
    public boolean isAvailable() {
        return this.status == MenuStatus.AVAILABLE && this.deletedAt == null;
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
